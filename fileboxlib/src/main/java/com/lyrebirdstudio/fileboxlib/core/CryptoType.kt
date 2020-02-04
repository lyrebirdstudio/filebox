package com.lyrebirdstudio.fileboxlib.core


enum class CryptoType(val typeName: String) {
    CONCEAL("conceal"), NONE("");

    companion object {
        fun fromTypeName(typeName: String): CryptoType {

            for (types in values()) {
                if (types.typeName.toLowerCase() == typeName.toLowerCase()) {
                    return types
                }
            }
            return NONE
        }
    }

}