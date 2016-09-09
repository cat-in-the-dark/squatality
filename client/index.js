(function() {
  'use strict';
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
  handler[Messages.ServerHelloMessage] = (data) => {
    console.log('ServerHelloMessage handler');
    const msg = {'className': Messages.HelloMessage, data: {name: 'Browser'}};
    socket.emit(eventName, JSON.stringify(msg));
  };

  handler[Messages.GameStateMessage] = (data) => {

  };

  handler[Messages.GameStartedMessage] = (data) => {
    console.log('GameStartedMessage handler')
  };
})();
