package mok.it.app.mokapp.model

enum class BadgeCategory {
    UNIVERZALIS {
        override fun toString(): String {
            return "Univerzális"
        }
    },
    FELADATSOR {
        override fun toString(): String {
            return "Feladatsor"
        }
    },
    GRAFIKA {
        override fun toString(): String {
            return "Grafika"
        }
    },
    KREATIV {
        override fun toString(): String {
            return "Kreatív"
        }
    },
    IT {
        override fun toString(): String {
            return "IT"
        }
    },
    PEDAGOGIA {
        override fun toString(): String {
            return "Pedagógia"
        }
    }
}