package mok.it.app.mokapp.model

import mok.it.app.mokapp.utility.Utility.unaccent

enum class Category {
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
    };

    companion object {
        fun String.toCategory() = Category.valueOf(this.unaccent().uppercase())
    }
}