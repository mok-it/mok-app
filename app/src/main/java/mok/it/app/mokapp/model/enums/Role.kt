package mok.it.app.mokapp.model.enums

enum class Role {
    /**
     * A premökös és a mökös is ide tartozik
     */
    BASIC_USER {
        override fun toString() = "Tag"
    },
    PROJECT_LEADER {
        override fun toString() = "Projektvezető"
    },
    AREA_MANAGER {
        override fun toString() = "Területvezető"
    },

    /**
     * Elmökségi tag / olyasvalaki, akinek jó, ha mindenhez van joga, pl. az app fő fejlesztői
     */
    ADMIN {
        override fun toString() = "Adminisztrátor"
    };
}