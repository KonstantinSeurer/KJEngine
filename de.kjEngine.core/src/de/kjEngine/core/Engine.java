package de.kjEngine.core;

import java.lang.module.ModuleDescriptor.Requires;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONObject;

import de.kjEngine.audio.Audio;
import de.kjEngine.audio.AudioContext;
import de.kjEngine.audio.openal.OpenALContext;
import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsContext;
import de.kjEngine.graphics.vulkan.VulkanContext;
import de.kjEngine.io.serilization.Serialize;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.ui.UISceneManager;
import de.kjEngine.ui.Window;
import de.kjEngine.util.DefaultExecutor;
import de.kjEngine.util.Executor;
import de.kjEngine.util.Time;
import de.kjEngine.util.Timer;

public class Engine {

	private Engine() {
	}

	public static class Settings {

		public static class Window {
			public static enum RefreshMode {
				VSYNC, UNLIMITED
			}

			@Serialize
			public int width = 1600, height = 900;
			@Serialize
			public String title = "KJEngine";
			@Serialize
			public boolean resizable = true;
			@Serialize
			public boolean showFramerate = true;
			@Serialize
			public RefreshMode refreshMode = RefreshMode.VSYNC;
		}

		public Window window = new Window();
		public ImplementationProvider implementationProvider = new ImplementationProvider() {

			@Override
			public GraphicsContext createGraphicsContext() {
				return new VulkanContext();
			}

			@Override
			public AudioContext createAudioContext() {
				return new OpenALContext();
			}
		};

		public List<StateHandler> stateHandlers = new ArrayList<>();

		public JSONObject additional = new JSONObject();

		public Settings() {
		}
	}

	private static Runnable onFrame = new Runnable() {

		boolean hasRun;

		@Override
		public void run() {
			if (frameTimer.time()) {
				boolean resized = Graphics.getContext().pollImage();

				if (hasRun && !resized) {
					// update descriptors
					for (StateHandler s : stateHandlers) {
						s.updateDescriptors();
					}

					// submit tasks to the GPU
					RenderingContext.flush();
					presentCommandBuffer.submit();
					Graphics.getContext().flush();
				}
				if (resized) {
					Graphics.getContext().finish();
					RenderingContext.resize(Window.getWidth(), Window.getHeight());
				}

				// update
				for (int i = 0; i < updateSubSteps; i++) {
					for (StateHandler s : stateHandlers) {
						s.update(delta / updateSubSteps);
					}
				}

				// generate new tasks
				RenderList renderList = new RenderList();
				for (StateHandler s : stateHandlers) {
					s.render(renderList);
				}
				presentCommandBuffer.clear();
				presentCommandBuffer.copyTexture2D(RenderingContext.render(renderList), Graphics.getContext().getScreenBuffer().getColorAttachment(null));

				// update window
				Window.update();
				if (settings.window.showFramerate && frameRateShowTimer.time()) {
					Window.setTitle(settings.window.title + " | " + getCurrentFrameRate() + " fps");
				}

				// recalculate frame time
				long now = Time.nanos();
				delta = Time.nanosToSeconds(now - lastTime);
				lastTime = now;

				// check for close request
				if (Window.isCloseRequested()) {
					stop();
					return;
				}

				hasRun = true;

				executer.queue(onFrame);
			}
		}
	};

	private static boolean running = false;
	private static List<StateHandler> stateHandlers = new CopyOnWriteArrayList<>();
	private static Settings settings;

	private static float delta = 0f;
	private static Timer frameTimer;
	private static long lastTime;

	private static Timer frameRateShowTimer = new Timer(Time.secondsToNanos(0.2f));

	private static Executor executer = new DefaultExecutor();

	private static CommandBuffer presentCommandBuffer;

	public static int updateSubSteps = 1;

	public static void stop() {
		if (!running) {
			throw new IllegalStateException("already stopped");
		}
		running = false;
	}

	public static void start(Settings settings, Module module) {
		if (running) {
			throw new IllegalStateException("already running");
		}

		Engine.settings = settings;
		stateHandlers.addAll(settings.stateHandlers);
		running = true;

		init(settings, module);

		for (StateHandler s : stateHandlers) {
			s.init();
		}

		executer.queue(onFrame);

		lastTime = Time.nanos();
		while (running) {
			executer.sync();
		}

		for (StateHandler s : stateHandlers) {
			s.dispose();
		}
		dispose();
	}

	private static void dispose() {
		presentCommandBuffer.dispose();

		Window.dispose();
		Audio.getContext().dispose();
		System.exit(0);
	}

	private static void init(Settings settings, Module module) {
		Set<Module> usedModules = new HashSet<>();
		getDependencies(module, usedModules);
		for (Module m : usedModules) {
			Class<?> initializer = Class.forName(m, m.getName() + ".ModuleInitializer");
			if (initializer != null) {
				try {
					initializer.getMethod("init").invoke(null);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		}

		Window.create(settings.window.width, settings.window.height, settings.window.title, settings.window.resizable, settings.implementationProvider.createGraphicsContext());
		Audio.init(settings.implementationProvider.createAudioContext());
		Set<ID> requiredRenderImplementations = new HashSet<>();
		for (StateHandler s : stateHandlers) {
			s.initRenderer(requiredRenderImplementations);
		}
		RenderingContext.init(requiredRenderImplementations);

		switch (settings.window.refreshMode) {
		case UNLIMITED:
			frameTimer = new Timer(0);
			break;
		case VSYNC:
			frameTimer = new Timer(Time.secondsToNanos(1f / 60f));
			break;
		}

		stateHandlers.add(new StateAdapter() {

			@Override
			public void update(float delta) {
				SceneManager.update(delta);
				UISceneManager.update(delta);
			}

			@Override
			public void render(RenderList renderList) {
				SceneManager.render(renderList);
				UISceneManager.render(renderList);
			}

			@Override
			public void updateDescriptors() {
				SceneManager.updateDescriptors();
				UISceneManager.updateDescriptors();
			}
		});

		presentCommandBuffer = Graphics.createCommandBuffer(CommandBuffer.FLAG_DYNAMIC);
	}

	private static void getDependencies(Module module, Set<Module> target) {
		Set<Requires> dependencies = module.getDescriptor().requires();
		for (Requires dependency : dependencies) {
			String name = dependency.name();
			if (name.startsWith("de.kjEngine")) {
				Module m = module.getLayer().findModule(name).get();
				target.add(m);
				getDependencies(m, target);
			}
		}
	}

	public static void addStateHandler(StateHandler e) {
		stateHandlers.add(e);
	}

	public static void removeStateHandler(StateHandler e) {
		stateHandlers.remove(e);
	}

	public static boolean isRunning() {
		return running;
	}

	public static Settings getSettings() {
		return settings;
	}

	public static float getDelta() {
		return delta;
	}

	public static int getCurrentFrameRate() {
		return (int) (1f / delta);
	}

	public static Executor getExecuter() {
		return executer;
	}
}
