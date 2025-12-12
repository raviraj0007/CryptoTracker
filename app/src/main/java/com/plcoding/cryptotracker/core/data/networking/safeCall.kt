package com.plcoding.cryptotracker.core.data.networking

import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.core.domain.util.Result
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import kotlin.coroutines.coroutineContext

// ✅ Removed withContext - let the caller handle threading
suspend inline fun <reified T> safeCall(
    execute: suspend () -> HttpResponse
): Result<T, NetworkError> {
    println("🔵 safeCall executing on thread: ${Thread.currentThread().name}")

    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        println("❌ safeCall: No Internet")
        return Result.Error(NetworkError.NO_INTERNET)
    } catch (e: SerializationException) {
        println("❌ safeCall: Serialization error - ${e.message}")
        return Result.Error(NetworkError.SERIALIZATION)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        println("❌ ViewModel: Unknown error - ${e.message}")
        return Result.Error(NetworkError.UNKNOWN)
    }

    println("✅ safeCall: Response received, converting to result")
    return responseToResult(response)
}

//package com.plcoding.cryptotracker.core.data.networking
//
//import com.plcoding.cryptotracker.core.domain.util.NetworkError
//import com.plcoding.cryptotracker.core.domain.util.Result
//import io.ktor.client.statement.HttpResponse
//import io.ktor.util.network.UnresolvedAddressException
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ensureActive
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.SerializationException
//import kotlin.coroutines.coroutineContext
//
//// ✅ FIX: Wrap entire function in withContext(Dispatchers.IO)
//// Added 'crossinline' to prevent non-local returns from execute lambda
//suspend inline fun <reified T> safeCall(
//    crossinline execute: () -> HttpResponse
//): Result<T, NetworkError> = withContext(Dispatchers.IO) {
//    println("🔵 safeCall executing on thread: ${Thread.currentThread().name}")
//
//    val response = try {
//        execute()
//    } catch (e: UnresolvedAddressException) {
//        println("❌ safeCall: No Internet")
//        return@withContext Result.Error(NetworkError.NO_INTERNET)
//    } catch (e: SerializationException) {
//        println("❌ safeCall: Serialization error - ${e.message}")
//        return@withContext Result.Error(NetworkError.SERIALIZATION)
//    } catch (e: Exception) {
//        coroutineContext.ensureActive()
//        println("❌ safeCall: Unknown error - ${e.message}")
//        return@withContext Result.Error(NetworkError.UNKNOWN)
//    }
//
//    println("✅ safeCall: Response received, converting to result")
//    responseToResult(response)
//}