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
    

## Error handling Recommendations with tips & tricks

The following recommendation works best with MVVM architecture, a separate data layer and with coroutines.

#### Data Layer

In the Data Layer, each inner method or layer (if there are multiple layers, ex. use-cases) should annotate its methods with the corresponding @Throws annotation and it should throw the exception instead of wrapping it in any kind of wrapper to make it easier to combine method calls without ending up nesting method calls. Only the use-case, or the last step before sending back the result to the ViewModel, should wrap the result. 

The data layer should define a root exception for all data related exceptions, with **error-handler** library this is the `DataLayerException`, from **error-handler-core**. For remote sources, a child hierarchy can be created, in our case, this is the `RemoteException`, from **error-handler-rest**, and a remote source (together with the Retrofit service) should throw only
remote-related exceptions. Other layers from the data layer, which are not related to remote, should be annotated with the more generic `DataLayerException`, since you can have other exceptions as well, ex. in repository you could have local data related errors or exceptions as well.

In the last step, before reaching back to the ViewModel the result should be wrapped into a Result, which has Success or Failure states. The **error-handler-core** provides a ResultWrapper class which has 3 cases (Success, Error, Exception). Having two separate cases for failures has the advantage that it is possible to differentiate an expected error from an unexpected one. Imagine an endpoint (.../users/{id}). When a user id is passed, it is an expected outcome that the id is
not valid and a user with the given id couldn't be found, however, a malformed response is not expected. With this approach, the application can react better to different kinds of failures. In case of a failure, the use case should wrap to Error or Failure based on the type of the failure, with the provided example, the not found failure should be mapped to the Error while the parsing error should be handled as an Exception.

**error-handler-rest** provides a utility method, namely **wrapToResult**, which does this mapping. If you have your own Result wrapping, then stick to it, otherwise, it is recommended to introduce such a mechanism for easier and more clear error handling.

#### View - ViewModel

 If the data layer exposes only this result, the ViewModel can easily handle all the possible cases, ex:
 
```kotlin
when (val result = getDataUseCase()) {
    is ResultWrapper.Success -> processData(result.data)
    is ResultWrapper.Error -> processError(result.exception) // Exception is a DataLayerException
    is ResultWrapper.Exception -> processException(result.throwable) // All generic exceptions which weren't expected at this point.
}
```
Since the ViewModel should only expose data, it should consume and transform any error coming from the data layer in a consumable form to the View. Here, a lot of things depend on the exact use-case and on the project, but generally speaking, usually some informative message is presented to the user, for example, in form of a SnackBar or Alert or in many other forms.

To keep the result processing and the error handling in the ViewModel, where it belongs, and since a lot of errors are general for almost all of the data layer, it is recommended to create some base error parser/processor. Continuing with Snackbar, but the base idea would work for other solutions as well, an ErrorItem model class can be created which can be easily consumed by the View.

```kotlin
data class ErrorItem(
    val messageRes: Int,
    val actionStringRes: Int? = null,
    ...
)
```

To handle the common errors in a single place, without duplicating the same logic over and over in all the ViewModels which interact with the data layer, a base error handler implementation is recommended. This can be an injectable class or just a simple top-level function, whichever works best for your project. 
(To get a better idea, check out the sample app).

```kotlin
fun determineUserFriendlyErrorFromResult(result: ResultWrapper.Error): ErrorItem {
    ...
}
``` 

This method should handle all the common errors (or error codes, depending on your REST API), generic status codes and so on. After this, your ViewModel should check for specific errors for this data layer call and let the common **handler** to handle the error. And as the last step, the ViewModel has to expose the error to the View (this can be done in many ways, it’s up to you which do you prefer). The View now has the function to consume this exposed error and to present it to the user. This is a repetitive task as well, so it is highly recommended not to duplicate this. It’s up to your project to find a way where it can be reused by all the view classes (example, if you have a base activity/fragment, that seems the right place for such logic).

To see this structure in action, check the sample code for the error-handler library, the interesting part is in `ErrorItem`, `ErrorParserBaseImpl` classes.


## How to contribute

The project uses [detekt](https://github.com/detekt/detekt), lint for static code verifications and [ktlint](https://ktlint.github.io/) for style check. We recommend the usage of the ktlint git hook (installation instructions can be found in the docs).

- Open a PR with changes with the following format:
  - Ticket/Issue if applicable
  - Short description of what is changes
  - Short description of how to test the introduced changes.
- Make sure `detekt` and `lint` passes for the new PR.
