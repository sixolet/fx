// Base class for fx
FxBase {
	var <syn, <slot, <params, <replacer, <drywet;

	// Virtual. Override me.
	addSynthdefs {
    
	}

	// Virtual. Override me.
	subPath {
		^"???";
	}

	// Virtual. Override me.
	symbol {
		^\fillIn;
	}

	handleSlot  { |newSlot, symbol|
		if ((newSlot != slot), {
			syn.free;
			syn = nil;
			replacer.free;
			replacer = nil;
			switch(newSlot)
			{\none} {
				(symbol++" set to: none").postln;
			}
			{\sendA} {
				if ((FxSetup.sendGroup.notNil && ~sendA.notNil), {
					syn = Synth.new(symbol, [
						\inBus, ~sendA,
						\outBus, Server.default.outputBus,
					] ++ params.asPairs, target: FxSetup.sendGroup);
					(symbol++" set to: send a").postln;
				});
			}
			{\sendB} {
				if ((FxSetup.sendGroup.notNil && ~sendB.notNil), {
					syn = Synth.new(symbol, [
						\inBus, ~sendB,
						\outBus, Server.default.outputBus,
					] ++ params.asPairs, target: FxSetup.sendGroup);
					(symbol++" set to: send b").postln;
				});
			}
			{\insert} {
				if (FxSetup.insertGroup.notNil, {                
					syn = Synth.new(symbol, [
						\inBus, Server.default.outputBus,
						\outBus, FxSetup.wet,
					] ++ params.asPairs, FxSetup.insertGroup, \addToTail);
					(symbol++" set to: insert").postln;

          replacer = Synth.new(\FxReplacer, [
						\inBus, FxSetup.wet,
						\outBus, Server.default.outputBus,
						\drywet, drywet
					], FxSetup.insertGroup, \addToTail);
				});
			}
		});
		slot = newSlot;
	}

	listenOSC {

		OSCFunc.new({|msg, time, addr, recvPort|
			var newSlot = msg[1].asSymbol;
			var symbol = this.symbol;
			this.handleSlot(newSlot, symbol);
		}, this.subPath ++ "/slot");

		OSCFunc.new({|msg, time, addr, recvPort|
			var key = msg[1].asSymbol;
			var value = msg[2].asFloat;
			params[key] = value;

			if (syn.notNil, {
				syn.set(key, value)
			});

			if (key == \drywet, {
				drywet = value;
				if (replacer.notNil, {
					replacer.set(\drywet, drywet)
				});
			});

		}, this.subPath ++ "/set");
	}
}
