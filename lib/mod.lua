local mod = require 'core/mods'

mod.hook.register("script_pre_init", "fx mod pre init", function()
    osc.send(
        { "localhost", 57120 },
        "/fxmod/init",
        {});
end)

mod.hook.register("script_post_cleanup", "fx mod post cleanup", function()
    osc.send(
        { "localhost", 57120 },
        "/fxmod/cleanup",
        {});
end)