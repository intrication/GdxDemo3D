package com.mygdx.game.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.components.ModelComponent;
import com.mygdx.game.components.MoveAimComponent;


/**
 * Created by user on 7/31/15.
 */
public class ModelRenderSystem extends EntitySystem {

	public Family systemFamily;
	private Vector3 pos = new Vector3();
	private ModelBatch modelBatch;
	private ImmutableArray<Entity> entities;
	private Camera camera;
	private ComponentMapper<ModelComponent> models = ComponentMapper.getFor(ModelComponent.class);
	private Environment environment;

	public ModelRenderSystem(Camera camera, Environment environment) {
		systemFamily = Family.all(ModelComponent.class).exclude(MoveAimComponent.class).get();
		modelBatch = new ModelBatch();
		this.camera = camera;
		this.environment = environment;

	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(systemFamily);
	}

	private boolean isVisible(final Camera camera, final ModelComponent cmp) {
		cmp.modelInstance.transform.getTranslation(pos);
		pos.add(cmp.center);
		return camera.frustum.sphereInFrustum(pos, cmp.radius);
	}

	@Override
	public void update(float deltaTime) {
		camera.update();
		modelBatch.begin(camera);
		for (int i = 0; i < entities.size(); ++i) {
			Entity entity = entities.get(i);
			ModelComponent cmp = models.get(entity);
			if (isVisible(camera, cmp)) {
				modelBatch.render(cmp.modelInstance, environment);
			}
		}
		modelBatch.end();
	}
}
