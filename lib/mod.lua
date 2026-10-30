local mod = require 'core/mods'

local function add_params()
  params:add_separator("fx_mod", "fx")
end

mod.hook.register("script_pre_init", "fx mod pre init", function()
  osc.send({ "localhost", 57120 }, "/fxmod/alloc");
end)

mod.hook.register("script_post_init", "_fx mod post init", function()
  osc.send({ "localhost", 57120 }, "/fxmod/init");
  add_params()
end)

mod.hook.register("script_post_cleanup", "fx mod post cleanup", function()
  osc.send({ "localhost", 57120 }, "/fxmod/cleanup");
end)
