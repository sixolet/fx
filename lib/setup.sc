FxSetup {
    classvar <sendA, <sendB, <wet, <fxGroup, <sendAGroup, <sendBGroup, <insertGroup, <plugins, <initOnce;
    *dynamicInit {
        if(fxGroup == nil, {
            fxGroup = Group.new(Server.default, addAction: \addToTail);
            sendAGroup = Group.new(fxGroup, addAction: \addToTail);
            sendBGroup = Group.new(fxGroup, addAction: \addToTail);
            insertGroup = Group.new(fxGroup, addAction: \addToTail);
            ~sendAGroup = sendAGroup;
            ~sendBGroup = sendBGroup;
            ~insertGroup = insertGroup;
        });
    }
    *dynamicCleanup {
        fxGroup.free;
        fxGroup = nil;
        sendAGroup = nil;
        sendBGroup = nil;
        insertGroup = nil;
        ~sendAGroup = sendAGroup;
        ~sendBGroup = sendBGroup;
        ~insertGroup = insertGroup;        
    }

    *register { |p|
        "register % initOnce is %\n".postf(p, initOnce);
        plugins.add(p);
    }

    *initClass {
        initOnce = false;
        plugins = [];
        StartUp.add {
            sendA = Bus.audio(Server.default, numChannels: 2);
            sendB = Bus.audio(Server.default, numChannels: 2);
            wet = Bus.audio(Server.default, numChannels: 2);
            ~sendA = sendA;
            ~sendB = sendB;
            OSCFunc.new({ |msg, time, addr, recvPort|
                FxSetup.dynamicInit;
                "FX setup complete".postln;
            }, "/fxmod/init");
            OSCFunc.new({ |msg, time, addr, recvPort|
                FxSetup.dynamicCleanup;
                "FX cleanup complete".postln;
            }, "/fxmod/cleanup");      
            if (initOnce.not, {
                initOnce = true;
                "INIT THIS ONCE YO".postln;
                plugins.do { |p|
                    p.addSynthdefs;
                    p.listenOSC;
                };
            });                  
        };
    }
}