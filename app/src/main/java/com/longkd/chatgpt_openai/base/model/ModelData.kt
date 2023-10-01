package com.longkd.chatgpt_openai.base.model

enum class ModelData(val value: String) {
    /**
     * Most capable GPT-3 model. Can do any task the other models can do, often with higher quality,
     * longer output and better instruction-following. Also supports inserting completions within text.
     * MAX REQUEST: 4,000 tokens
     * */
    GPT_D("text-davinci-003"),
    GPT_D2("text-davinci-002"),

    /**
     * Very capable, but faster and lower cost than Davinci.
     * MAX REQUEST: 2,048 tokens
     * */
    GPT_C("text-curie-001"),

    /**
     * Capable of straightforward tasks, very fast, and lower cost.
     * MAX REQUEST:2,048 tokens
     * */
    GPT_B("text-babbage-001"),

    /**
     * Capable of very simple tasks, usually the fastest model in the GPT-3 series, and lowest cost.
     * MAX REQUEST: 2,048 tokens
     * */
    GPT_A("text-ada-001"),

    /**
     * Most capable Codex model. Particularly good at translating natural language to code.
     * In addition to completing code, also supports inserting completions within code.
     * MAX REQUEST: 8,000 tokens
     * */
    CODE_A("code-davinci-002"),

    /**
     * Almost as capable as Davinci Codex, but slightly faster. This speed advantage may make it preferable for real-time applications.
     * MAX REQUEST: 2,048 tokens
     * */
    CODE_C("code-cushman-001"),
    GPT_35("gpt-3.5-turbo"),
}