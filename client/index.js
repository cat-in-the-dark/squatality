(function() {
  'use strict';

  initConnection();

  function State(sync) {
    this.gameState = null;
    this.moveMessage = null;
    this.sync = sync;
  }

  State.prototype.onMove = function (speed, angle, stateName) {
    if (this.moveMessage) {
      this.moveMessage.speedX += speed.x;
      this.moveMessage.speedY += speed.y;
      this.moveMessage.angle += angle;
      this.moveMessage.stateName = stateName;
    } else {
      this.moveMessage = {speedX: speed.x, speedY: speed.y, angle: angle, stateName: 'RUNNING'};
    }
  };

  function initConnection() {
    const eventName = 'message';
    const socket = io('http://localhost:8080');
    socket.on('connect', () => {
      console.log('Connected');
    });
    socket.on(eventName, (data) => {
      const msg = JSON.parse(data);
      try {
        handler[msg.className](msg.data);
      } catch(e) {
        console.error(`Can't handle message ${data}: ${e}`);
      }
    });

    const Messages = {
      EnemyDisconnectedMessage: 'com.catinthedark.models.EnemyDisconnectedMessage',
      GameStartedMessage: 'com.catinthedark.models.GameStartedMessage',
      RoundEndsMessage: 'com.catinthedark.models.RoundEndsMessage',
      HelloMessage: 'com.catinthedark.models.HelloMessage',
      ServerHelloMessage: 'com.catinthedark.models.ServerHelloMessage',
      MoveMessage: 'com.catinthedark.models.MoveMessage',
      GameStateMessage: 'com.catinthedark.models.GameStateMessage',
      SoundMessage: 'com.catinthedark.models.SoundMessage',
      ThrowBrickMessage: 'com.catinthedark.models.ThrowBrickMessage'
    };
    const handler = {};

    let state = null;
    handler[Messages.ServerHelloMessage] = (data) => {
      console.log('ServerHelloMessage handler');
      const msg = {'className': Messages.HelloMessage, data: {name: 'Browser'}};
      socket.emit(eventName, JSON.stringify(msg));
    };

    handler[Messages.GameStateMessage] = (data) => {
      if (state == null) return;
      state.gameState = data.gameStateModel;
    };

    handler[Messages.GameStartedMessage] = (data) => {
      console.log('GameStartedMessage handler');
      state = new State(() => {
        const msg = {'className': Messages.MoveMessage, data: state.moveMessage};
        socket.emit(eventName, JSON.stringify(msg));
        state.moveMessage = null;
      });
      initGame(state);
    };
  }

  /**
  * Yes. We connect network and game by state object. It's mutable!
  **/
  function initGame(state) {
    const game = new Phaser.Game(1081, 652, Phaser.WEBGL, '', { preload: preload, create: create, update: update });
    let syncTimer = 0;
    function preload() {
      game.load.spritesheet('gop', 'gop_blue.png', 108, 108, 19);
    }

    function releaseUnit(x, y) {
      const sprite = game.add.sprite(x, y, 'gop');
      sprite.animations.add('run', [0, 1, 2, 3, 4, 5, 6]);
      sprite.animations.add('idle', [0]);
      sprite.animations.play('idle', 10, true);
      return sprite;
    }

    let player = null;
    function create() {
      player = releaseUnit(0,0);
    }

    function update() {

      let speed = {x: 0.0, y: 0.0};
      let stateName = 'IDLE';
      if (game.input.keyboard.isDown(Phaser.Keyboard.A)) {
        speed.x -= 5.0;
      }
      if (game.input.keyboard.isDown(Phaser.Keyboard.D)) {
        speed.x += 5.0;
      }
      if (game.input.keyboard.isDown(Phaser.Keyboard.W)) {
        speed.y -= 5.0;
      }
      if (game.input.keyboard.isDown(Phaser.Keyboard.S)) {
        speed.y += 5.0;
      }
      if (speed.x != 0.0 || speed.y != 0) stateName = 'RUNNING';
      state.onMove(speed, 0, stateName);

      syncTimer += this.time.elapsedMS;
      if (syncTimer > 40) {
        state.sync(syncTimer);
        syncTimer = 0;
      }
      player.x = state.gameState.me.x;
      player.y = state.gameState.me.y;
      player.angle = state.gameState.me.angle;
      if (state.gameState.me.state == 'RUNNING') {
        player.animations.play('run', 10, true);
      } else {
        player.animations.play('idle', 10, true);
      }
    }
  }
})();
