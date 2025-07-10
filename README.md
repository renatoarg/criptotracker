# Crypto Tracker

Cryptocurrency tracker app.

## Environment
- **Android Studio**: Android Studio Narwhal | 2025.1.1
- **Compile SDK**: 36
- **Min SDK**: 24
- **Java version**: 17

## Project setup
- **API_KEY**: Insert your CoinGecko API KEY to run the application, check gradle.properties

## Run test
- **Execute gradle**: ./gradlew clean testDebugUnitTest

## Run app (CI-ready):
- **Execute gradle**: ./gradlew build

## Requirements

**Tech stack**
- Kotlin ✅
- Jetpack Compose (all UI must use Compose) ✅
- MVVM (with ViewModel) ✅
- Retrofit for API calls ✅
- Room for local storage ✅
- Coroutines / Flow ✅

**Functional**
- Fetch live Crypto Coin list and details from a public API (e.g.https://docs.coingecko.com/reference/introduction) ✅
- Show cached data immediately when the app opens ✅
- Have a swipe-to-refresh action in the Coins list that triggers API fetch and updates Room + UI ✅

**Compose UI**
- Coins list: symbol and name ✅
- Detail screen: image, symbol, name, description, and price (USD) ✅
- Indication if data is loading (e.g., circular progress) ✅
- Indication if API call failed (e.g., toast / snackbar) ✅

**Extra credit / bonus**
- Add basic unit tests ✅
- Provide a README describing project structure ✅
- Provide CI-ready code (clean build via ./gradlew build) ✅

**Constraints**
- App should be functional but minimal; no need for advanced UI polish ✅
- No need to use a full navigation architecture (e.g., Navigation Compose) simple manual navigation is fine ✅
- No external architecture frameworks or DI tools (e.g., Hilt, Koin) ✅

**Deliverable**
GitHub repo or zip with:
- Working Android app code ✅
- Brief README ✅
- Instructions to run ✅
