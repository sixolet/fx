FxSetup {
    classvar <sendA, <sendB, <wet, <fxGroup, <sendGroup, <insertGroup, plugins, initOnce;

    *alloc {
        if(initOnce.isNil, {
            initOnce = true;
            sendA = Bus.audio(Server.default, numChannels: 2);
            sendB = Bus.audio(Server.default, numChannels: 2);
            wet = Bus.audio(Server.default, numChannels: 2);
            ~sendA = sendA;
            ~sendB = sendB;
			"INIT THIS ONCE YO".postln;
        })
    }

    *dynamicInit {
        if(fxGroup.isNil, {
            fxGroup = Group.new(Server.default, \addToTail);
            insertGroup = Group.new(fxGroup);
            sendGroup = Group.new(fxGroup);
			"FX setup complete".postln;
        })
    }

    *dynamicCleanup {
        if(fxGroup.notNil, {
            fxGroup.free;
            fxGroup = nil;
            sendGroup = nil;
            insertGroup = nil;
			"FX cleanup complete".postln;
        })
    }

    *register { |p|
        plugins = plugins.add(p);
        "registered: %\n".postf(p.class.name);
    }

    *initClass {

        plugins = [];

        StartUp.add {

            OSCFunc.new({ |msg, time, addr, recvPort|
                FxSetup.alloc;
            }, "/fxmod/alloc");

            OSCFunc.new({ |msg, time, addr, recvPort|
                FxSetup.dynamicInit;
            }, "/fxmod/init");

            OSCFunc.new({ |msg, time, addr, recvPort|
                FxSetup.dynamicCleanup;
            }, "/fxmod/cleanup");

            SynthDef(\FxReplacer, {|inBus, outBus, drywet|
				XOut.ar(outBus, drywet, In.ar(inBus, 2));
				ReplaceOut.ar(inBus, Silent.ar(2));
			}).add;

			plugins.do { |p|
				"installing: %\n".postf(p.class.name);
				p.addSynthdefs;
				p.listenOSC;
			};

        }
    }

}
