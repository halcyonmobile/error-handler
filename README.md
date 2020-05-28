## Structure
  The library consist of 2 modules:
  - **error-handler-core** - defines the DataLayerException, the root exception for an entire data layer, and ResultWrapper, wrapper class based on Railway Oriented Programming pattern.
  - **error-handler-rest** - which provides error handling for REST API consumption. Note that this module is built on top of [retrofit-error-wrapping](https://github.com/halcyonmobile/retrofit-error-wrapping) library. This module provides subclasses for DataLayerException with possible failures that can occur during an HTTP call.

## Setup
  ###### Latest stable version is [ ![Download](https://api.bintray.com/packages/halcyonmobiledevteam/maven/error-handler:core/images/download.svg?version=0.1.0) ](https://bintray.com/halcyonmobiledevteam/maven/error-handler:core/0.1.0/link)
  - Add `jcenter` to your project `build.gradle`
   ```kotlin 
repositories {
       // other repositories
       jcenter()
}
   ``` 
   - Add this library to your project
   ```kotlin
dependencies {
       // if you need core module only
       implementation "com.halcyonmobile.error-handler:core:<latest_version>"
       // if you need both core module + rest module
       implementation "com.halcyonmobile.error-handler:rest:<latest_version>"
   }
   ```
   - Use the `RestCallAdapter.Builder` and build your own `CallAdapterFactory` for Retrofit2
   ```kotlin
val callAdapterFactory = RestCallAdapterFactory.Builder()
       /* Customize it here. Check its documentation for more */
       .build()
   ```
   - Add it to `Retrofit`
   ```kotlin
Retrofit.Builder()
        .addCallAdapterFactory(callAdapterFactory)
        /* other setups */
        .build()
   ```
   - Build an `ApiErrorModel`, that represents the model of error that's being returned by your backend, in case something goes wrong.
   
   - Mark your retrofit methods with @ParsedError, passing your custom ApiErrorModel from the previous step (*more about this under the **How it works** section below*)
   ```kotlin
   interface AuthenticationService {
       
       //Tip! @ParsedError means this method might throw a RemoteException,
       //so it's highly recommended to also add @Throw(RemoteException::class)
       @ParsedError(MyApiErrorModel::class)
       @POST("auth/login")
       suspend fun login(@Body loginBody: LoginRequestBody): UserDto
   }
   ```
   
   - That's it! No more hand-done conversions!

## How it works
   - When you annotate your retrofit call with the @ParsedError annotation, any exception that will be thrown will automatically be converted to a RemoteException type. If you receive an error body from your API, the library will try to parse the body into your model class provided in the @ParsedError annotation and if the parsing succeeds, then it will throw an ApiError with the parsed body (you will have to cast to your model class when you want to retrieve it).
  - If the above parsing failed for any reason, then if the API responded with a 500 Status code, your method will throw an InternalServerError, otherwise an ErrorPayloadParsingException with the status code of the response will be thrown.
- You can receive two connectivity related errors, one when the device is not connected to the network and one when a timeout happens.
- If any other error/exception occurs during the HTTP call, your method will throw a ServerCommunicationException, wrapping the original exception. 

## Configuration

  - **I want to handle serialization related exceptions.**
    - **error-handler** is library independent, and by default any unknown exception will be wrapped into a **ServerCommunicationException**. If you want to handle your own serialization exception, ex. A JsonDataException with Moshi, then you have to provide a  **SerializationExceptionConverter** implementation which converts your exact serialization exception to a generic one which will be consumed by the call adapter. You can do all this in 1 step, in the builder, like this:
      
      ```kotlin
      RestHandlerCallAdapter.Builder()
          .addSerializationConverter(object : SerializationExceptionConverter {
              override fun convert(networkException: NetworkException): NetworkException {
                  // Do your conversion here!
                  return YourCustomNetworkException
              }
          })
      ```
    
      
  - **I have a business requirement to log errors which occur with the network call.**
    - In that case, you just have to provide a NetworkErrorLogger implementation to the RestCallAdapter.Builder . There's  no limit on how many NetworkErrorLoggers you can have.
  - **I don’t like working with Any for the error body in the ApiError, I want a model class.**
    - Don’t worry, we have you covered! In this case you have to provide a ParsedApiErrorConverter implementation where you receive that Any and you can convert to your own exception with any model you want.
  - **I have a custom exception instead of ApiError, but I want to log it.**
    - Nothing is easier than that. Just make sure that your custom exception implements the Loggable interface, and the rest is handled during the error conversion. Every RemoteException which implements the Loggable interface is guaranteed to be logged.
  - **I don’t want to handle all of the possible exceptions with `try {} catch() {}` outside the data layer.**
    - In this case, you can use the ResultWrapper from the error-handler-core together with the wrapToResult helper function, if you don’t already have such a mechanism in your project.
