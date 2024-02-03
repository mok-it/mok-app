package mok.it.app.mokapp.model

import mok.it.app.mokapp.utility.Utility.unaccent

/**
 * The possible enum values of the category field in the Project class.
 * This is necessary to make the code type safe.
 * If you want to add a new category, make sure it is the unaccented,
 * spaceless, uppercase version of the toString method's return value.
 */
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
    },
    NYARITABORIELOKESZITES {
        override fun toString(): String {
            return "Nyári tábori előkészítés"
        }
    },
    EVKOZITABORIELOKESZITES {
        override fun toString(): String {
            return "Évközi tábori előkészítés"
        }
    };

    companion object {
        fun String.toCategory() = Category.valueOf(this.replace(" ", "").unaccent().uppercase())
        fun toList(): ArrayList<String> {
            val categoryList = ArrayList<String>()
            Category.values().forEach {
                categoryList.add(it.toString())
            }
            return categoryList
        }
    }
}