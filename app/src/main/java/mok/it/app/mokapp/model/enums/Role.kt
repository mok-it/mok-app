package mok.it.app.mokapp.model.enums

enum class Role {
    ADMIN {
        //elmökségi tag / olyasvalaki, akinek jó, ha mindenhez van joga, pl. az app fő fejlesztői
        override fun toString() = "Adminisztrátor"
    },

    AREA_MANAGER {
        override fun toString() = "Területvezető"
    },
    PROJECT_LEADER {
        override fun toString() = "Projektvezető"
    },
    BASIC_USER {
        override fun toString() = "Tag" // a premökös és a mökös is ide tartozik
    };
}