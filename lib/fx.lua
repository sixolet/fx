local Fx = {}

Fx.slot_symbols = {"none", "sendA", "sendB", "insert"}

function Fx:new(o)
    o = o or {}
    setmetatable(o, self)
    self.__index = self
    return o
end

function Fx:install()
    local old_init = init
    init = function()
        old_init()
        self:add_params()
    end
end

function Fx:add_control(id, name, key, spec)
    params:add_control(id, name, spec)
    params:set_action(id, function(val)
        osc.send({ "localhost", 57120 }, self.subpath.."/set", {key, val})
    end)
end

function Fx:add_taper(id, name, key, min, max, default, k, units)
    params:add_taper(id, name, min, max, default, k, units)
    params:set_action(id, function(val)
        osc.send({ "localhost", 57120 }, self.subpath.."/set", {key, val})
    end)
end

function Fx:add_slot(id, name)
    params:add_option(id, name, {"none", "send a", "send b", "insert"}, 1)
    self:add_control(id.."_drywet", "dry/wet", "drywet", controlspec.new(0, 1, 'lin', 0, 1, 0))
    params:set_action(id, function(val)
        if val == 4 then
            params:show(id.."_drywet")
        else
            params:hide(id.."_drywet")
        end
       -- print("send slot", self.subpath.."/slot", Fx.slot_symbols[val])
        osc.send({ "localhost", 57120 }, self.subpath.."/slot", {Fx.slot_symbols[val]})
        _menu.rebuild_params()
    end)
end

return Fx