package com.lyrebirdstudio.fileboxlib.core

class FileBoxConfig private constructor(
    val timeToLiveInMillis: Long,
    val directoryType: DirectoryType,
    val folderName: String,
    val cryptoType: CryptoType
) {

    class FileBoxConfigBuilder {

        private var timeToLiveInMillis: Long = Defaults.timeToLive()

        private var directoryType = Defaults.directory()

        private var folderName = Defaults.folderName()

        private var cryptoType = Defaults.cryptoType()

        fun setTTLInMillis(timeToLiveInMillis: Long): FileBoxConfigBuilder {
            this.timeToLiveInMillis = timeToLiveInMillis
            return this
        }

        fun setDirectory(directoryType: DirectoryType): FileBoxConfigBuilder {
            this.directoryType = directoryType
            return this
        }

        @Deprecated(message = "Setting a folder name will cause a creating a new database. Don't use this unless you debugging.")
        fun setFolderName(folderName: String): FileBoxConfigBuilder {
            this.folderName = folderName
            return this
        }

        fun setCryptoType(cryptoType: CryptoType): FileBoxConfigBuilder {
            this.cryptoType = cryptoType
            return this
        }

        fun build(): FileBoxConfig {
            return FileBoxConfig(
                timeToLiveInMillis = timeToLiveInMillis,
                directoryType = directoryType,
                folderName = folderName,
                cryptoType = cryptoType
            )
        }
    }

    companion object {

        fun createDefault(): FileBoxConfig {
            return FileBoxConfigBuilder()
                .setDirectory(Defaults.directory())
                .setTTLInMillis(Defaults.timeToLive())
                .setFolderName(Defaults.folderName())
                .setCryptoType(Defaults.cryptoType())
                .build()
        }

    }
}

