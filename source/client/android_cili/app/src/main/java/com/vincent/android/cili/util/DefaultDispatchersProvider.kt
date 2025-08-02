
package com.vincent.android.cili.util

import kotlinx.coroutines.Dispatchers

class DefaultDispatchersProvider : DispatchersProvider {

  override fun io() = Dispatchers.IO
  override fun main() = Dispatchers.Main
}