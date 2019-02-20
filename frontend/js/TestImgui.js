SystemJS.config({
  map: {
    "imgui-js": "/imgui-js"
  },
  packages: {
    "imgui-js": { main: "imgui.js" }
  }
});
let ImGui;
let ImGui_Impl;
Promise.resolve().then(() => {
  return System.import("imgui-js").then((module) => {
    ImGui = module;
    return ImGui.default();
  });
}).then(() => {
  return System.import("imgui-js/example/imgui_impl").then((module) => {
    ImGui_Impl = module;
  });
}).then(() => {
  const canvas = document.getElementById("output");
  const devicePixelRatio = window.devicePixelRatio || 1;
  canvas.width = canvas.scrollWidth * devicePixelRatio;
  canvas.height = canvas.scrollHeight * devicePixelRatio;
  window.addEventListener("resize", () => {
    const devicePixelRatio = window.devicePixelRatio || 1;
    canvas.width = canvas.scrollWidth * devicePixelRatio;
    canvas.height = canvas.scrollHeight * devicePixelRatio;
  });

  ImGui.CreateContext();
  ImGui_Impl.Init(canvas);

  ImGui.StyleColorsDark();
  //ImGui.StyleColorsClassic();

  const clear_color = new ImGui.ImVec4(0.3, 0.3, 0.3, 1.00);

console.log("passed");
  const renderer = new THREE.WebGLRenderer({ canvas: canvas });
  
  const scene = new THREE.Scene();
	
  const camera = new THREE.PerspectiveCamera(50, canvas.width / canvas.height, 0.1, 10000);
	camera.position.set(0, 0, 500);
	scene.add(camera);
  
  const light = new THREE.DirectionalLight(0xffffff, 0.8);
	light.position.set(0, 0, 350);
	light.lookAt(new THREE.Vector3(0, 0, 0));
	scene.add(light);

  const geometry = new THREE.BoxGeometry(150,150,150);
	const material = new THREE.MeshLambertMaterial({ color:0x00ff00, transparent: true });
	const mesh = new THREE.Mesh(geometry, material);
  mesh.name = "cube";
  scene.add(mesh);

  let done = false;
  window.requestAnimationFrame(_loop);
  function _loop(time) {
    ImGui.NewFrame();
    ImGui_Impl.NewFrame(time);

    ImGui.SetNextWindowPos(new ImGui.ImVec2(50, 50), ImGui.Cond.FirstUseEver);
    ImGui.SetNextWindowSize(new ImGui.ImVec2(294, 140), ImGui.Cond.FirstUseEver);
    ImGui.Begin("Debug");
    
    ImGui.ColorEdit4("clear color", clear_color);
    ImGui.Separator();
    ImGui.Text(`Scene: ${scene.uuid.toString()}`);
    ImGui.Separator();
    ImGui.Text(`Material: ${material.uuid.toString()}`);
    ImGui.ColorEdit3("color", material.color);
    const side_enums = [ THREE.FrontSide, THREE.BackSide, THREE.DoubleSide ];
    const side_names = {};
    side_names[THREE.FrontSide] = "FrontSide";
    side_names[THREE.BackSide] = "BackSide";
    side_names[THREE.DoubleSide] = "DoubleSide"
    if (ImGui.BeginCombo("side", side_names[material.side])) {
      side_enums.forEach((side) => {
        const is_selected = (material.side === side);
        if (ImGui.Selectable(side_names[side], is_selected)) {
          material.side = side;
        }
        if (is_selected) {
          ImGui.SetItemDefaultFocus();
        }
      });
      ImGui.EndCombo();
    }
    ImGui.Separator();
    ImGui.Text(`Mesh: ${mesh.uuid.toString()}`);
    ImGui.Checkbox("visible", (value = mesh.visible) => mesh.visible = value);
    ImGui.InputText("name", (value = mesh.name) => mesh.name = value);
    ImGui.SliderFloat3("position", mesh.position, -100, 100);
    ImGui.SliderAngle3("rotation", mesh.rotation);
    ImGui.SliderFloat3("scale", mesh.scale, -2, 2);

    ImGui.End();

    ImGui.EndFrame();

    ImGui.Render();
    
    renderer.setClearColor(new THREE.Color(clear_color.x, clear_color.y, clear_color.z), clear_color.w);
    renderer.setSize(canvas.width, canvas.height);
    camera.aspect = canvas.width / canvas.height;
    camera.updateProjectionMatrix();
    renderer.render(scene, camera);

    ImGui_Impl.RenderDrawData(ImGui.GetDrawData());

    // TODO: restore WebGL state in ImGui Impl
    renderer.state.reset();

    window.requestAnimationFrame(done ? _done : _loop);
  }

  function _done() {
    ImGui_Impl.Shutdown();
    ImGui.DestroyContext();
  }
});


