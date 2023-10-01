
package com.longkd.chatgpt_openai.open.dto.finetune

/**
 * Fine-tuning job hyperparameters
 *
 * https://beta.openai.com/docs/api-reference/fine-tunes
 */
class HyperParameters {
    /**
     * The batch size to use for training.
     */
    var batchSize: String? = null

    /**
     * The learning rate multiplier to use for training.
     */
    var learningRateMultiplier: Double? = null

    /**
     * The number of epochs to train the model for.
     */
    var nEpochs: Int? = null

    /**
     * The weight to use for loss on the prompt tokens.
     */
    var promptLossWeight: Double? = null
}