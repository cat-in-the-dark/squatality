(function() {
  'use strict';

  initConnection();

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
    const state = {gameState: null};
    handler[Messages.ServerHelloMessage] = (data) => {
      console.log('ServerHelloMessage handler');
      const msg = {'className': Messages.HelloMessage, data: {name: 'Browser'}};
      socket.emit(eventName, JSON.stringify(msg));
    };

    handler[Messages.GameStateMessage] = (data) => {
      state.gameState = data.gameStateModel;
    };

    handler[Messages.GameStartedMessage] = (data) => {
      console.log('GameStartedMessage handler');
      initGame(state);
    };
  }

  /**
  * Yes. We connect network and game by state object. It's mutable!
  **/
  function initGame(state) {
    const game = new Phaser.Game(1081, 652, Phaser.AUTO, '', { preload: preload, create: create, update: update });
    function preload() {
      game.load.spritesheet('gop', 'gop_blue.png', 108, 108, 19);
    }

    var sprite = null;
    function create() {
      sprite = game.add.sprite(0, 0, 'gop');
      sprite.animations.add('walk', [0, 1, 2, 3, 4, 5, 6]);
      sprite.animations.play('walk', 10, true);
    }

    function update() {
      sprite.x = state.gameState.me.x;
      sprite.y = state.gameState.me.y;
    }
  }
})();
