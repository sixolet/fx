// Base class for fx

FxBase {
    var <syn, <slot, <params, <replacer, <drywet;

    addSynthdefs {
        // Virtual. Override me.
    }

    // Virtual. Override me.
    subPath {
        ^"???";
    }

    // Virtual. Override me.
    symbol {
        ^\fillIn;
    }

    handleSlot  { |newSlot|
        if ((newSlot != slot), {
            syn.free;
            syn = nil;
            replacer.free;
            replacer = nil;
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
                if ( FxSetup.insertGroup != nil, {
                    replacer = Synth.new(\replacer, [
                        \in, FxSetup.wet,
                        \out, Server.default.outputBus,
                        \drywet, drywet,
                    ], target: FxSetup.insertGroup, addAction: \addToTail);
                    syn = Synth.new(this.symbol, [
                        \inBus, Server.default.outputBus,
                        \outBus, FxSetup.wet,
                        ] ++ params.asPairs, target: FxSetup.insertGroup, addAction: \addToHead);
                });                    
            }
        });
        slot = newSlot;
    }

    listenOSC {
        OSCFunc.new({|msg, time, addr, recvPort|
            var newSlot = msg[1].asSymbol;
            "considering slot % as opposed to %\n".postf(newSlot, slot);
            this.handleSlot(newSlot);
        }, this.subPath ++ "/slot");
        OSCFunc.new({|msg, time, addr, recvPort|
            var key = msg[1].asSymbol;
            var value = msg[2].asFloat;
            params[key] = value;
            if (syn != nil, {
                syn.set(key, value);
            });
            if (key == \drywet, {
                drywet = value;
                if (replacer != nil, {
                    replacer.set(\drywet, drywet);
                });
            });
        }, this.subPath ++ "/set");        
    }
}