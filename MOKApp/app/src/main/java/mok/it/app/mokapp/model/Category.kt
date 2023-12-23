package mok.it.app.mokapp.model

import mok.it.app.mokapp.utility.Utility.unaccent

enum class Category {
    UNIVERZALIS {
        override fun toString(): String {
            return "Univerzális"
        }
    },
    SZERVEZETFEJLESZTES {
        override fun toString(): String {
            return "Szervezetfejlesztés"
        }
    },
    FELADATSOR {
        override fun toString(): String {
            return "Feladatsor"
        }
    },
    MEDIAESDIY {
        override fun toString(): String {
            return "Média és DIY"
        }
    },
    TABORIPROGRAMESELOKESZITES {
        override fun toString(): String {
            return "Tábori program és előkészítés"
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
        fun String.toCategory() = Category.valueOf(this.replace(" ","").unaccent().uppercase())
    }
}