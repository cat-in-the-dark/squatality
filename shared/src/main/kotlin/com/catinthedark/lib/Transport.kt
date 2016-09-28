package com.catinthedark.lib

/**
 * Transport define the way how to deliver and handle messages from other systems.
 * @see IMessage
 */
interface Transport {
    /**
     * Send message to other systems.
     * @param msg is message to send
     */
    fun send(msg: IMessage)

    /**
     * Set subscriber for messages from other systems.
     * @param receiver is function to call on new message received.
     */
    fun setReceiver(receiver: (IMessage) -> Unit)
}
