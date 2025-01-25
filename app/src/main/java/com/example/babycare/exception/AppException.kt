sealed class AppException(message: String) : Exception(message) {
    class NetworkException(message: String) : AppException(message)
    class DatabaseException(message: String) : AppException(message)
    class ValidationException(message: String) : AppException(message)
    class FileException(message: String) : AppException(message)
} 