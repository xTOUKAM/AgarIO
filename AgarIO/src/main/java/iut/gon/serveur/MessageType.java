package iut.gon.serveur;

public enum MessageType {
    //From server
    SERVER_ID,
    SERVER_GAME_STATE,
    SERVER_INITIAL_GAME_STATE,
    SERVER_STOP,
    SERVER_ERROR,

    //From client
    CLIENT_STATUS,
    CLIENT_MOVEMENT,
    CLIENT_CHAT_MESSAGE,
    CLIENT_SPLIT,
}