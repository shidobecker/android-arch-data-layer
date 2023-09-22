# Android Architecture Data layer CodeLab
Codelab demonstrando a utilização de datalayer dentro do projeto android, utilizando:

Baseado em [Android Architecture DataLayer Codelab](https://github.com/android/architecture-samples/archive/refs/heads/data-codelab-start.zip)
[Codelab](https://developer.android.com/codelabs/building-a-data-layer?hl=en&authuser=1#0)

## Architecture

* Jetpack Compose para interface **[Jetpack Compose](https://developer.android.com/jetpack/compose)**
* Single Activity architecture utilizando **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)**
* Presentation layer contendo compose e ViewModel por tela
* UI reativa utilizando **[Flow](https://developer.android.com/kotlin/flow)** e **[coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** para operações assíncronas
* Data layer com repositório e duas fontes de dados (local utilizando Room e uma fake remota)
* Dois **product flavors**, `mock` e `prod`, [para facilitar o desenvolvimento e teste](https://android-developers.googleblog.com/2015/12/leveraging-product-flavors-in-android.html).
* Uma coleção de testes unitários, integração e e2e **tests**, incluindo testes "shared" que podem ser executados no emulador / dispositivo.
* Injeção de dependência usando [Hilt](https://developer.android.com/training/dependency-injection/hilt-android).

 

## Preview
 
