// Base class for fx

FxBase {
    var <syn, <slot, <params;

    addSynthdefs {
        // Virtual. Override me.
    }

    *dynamicInit {

    }

    // Virtual. Override me.
    subPath {
        ^"???";
    }

    // Virtual. Override me.
    symbol {
        ^\fillIn;
    }

    listenOSC {
        OSCFunc.new({|msg, time, addr, recvPort|
            var newSlot = msg[1].asSymbol;
            "considering slot % as opposed to %\n".postf(newSlot, slot);
            if ((newSlot != slot), {
                syn.free;
                "freeing".postln;
                switch (newSlot)
                {\none} {
                    // pass
                    "none".postln;
                }
                {\sendA} {
                    if ( (~sendAGroup != nil) && (~sendA != nil), {
                        syn = Synth.new(this.symbol, [
                                \inBus, ~sendA,
                                \outBus, Server.default.outputBus,
                                ] ++ params.asPairs, target: ~sendAGroup);
                    });
                    "a".postln;
                }
                {\sendB} {
                    if ( (~sendBGroup != nil) && (~sendB != nil), {
                        syn = Synth.new(this.symbol, [
                            \inBus, ~sendB,
                            \outBus, Server.default.outputBus,
                            ] ++ params.asPairs, target: ~sendBGroup);
                    });
                    "b".postln;
                }
                {\insert} {
                }
            });
            slot = newSlot;
        }, this.subPath ++ "/slot");
        OSCFunc.new({|msg, time, addr, recvPort|
            var key = msg[1].asSymbol;
            var value = msg[2].asFloat;
            params[key] = value;
            if (syn != nil, {
                syn.set(key, value);
            });
        }, this.subPath ++ "/set");        
    }
}