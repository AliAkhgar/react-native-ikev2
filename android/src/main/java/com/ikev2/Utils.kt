package com.ikev2

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import java.io.Serializable

public fun convertMixedMapToBundle(map: HashMap<String, Any?>?): Bundle {
  val bundle = Bundle()
  if (map != null) {
    for ((key, value) in map) {
      when (value) {
        is String -> bundle.putString(key, value)
        is Int -> bundle.putInt(key, value)
        is Boolean -> bundle.putBoolean(key, value)
        is Double -> bundle.putDouble(key, value)
        is Float -> bundle.putFloat(key, value)
        is Long -> bundle.putLong(key, value)
        is Char -> bundle.putChar(key, value)
        is Short -> bundle.putShort(key, value)
        is Byte -> bundle.putByte(key, value)

        // Arrays of primitives
        is IntArray -> bundle.putIntArray(key, value)
        is BooleanArray -> bundle.putBooleanArray(key, value)
        is DoubleArray -> bundle.putDoubleArray(key, value)
        is FloatArray -> bundle.putFloatArray(key, value)
        is LongArray -> bundle.putLongArray(key, value)
        is CharArray -> bundle.putCharArray(key, value)
        is ByteArray -> bundle.putByteArray(key, value)
        is ShortArray -> bundle.putShortArray(key, value)

        // ArrayLists (specific types supported by Bundle)
        is ArrayList<*> -> {
          // Check element type for ArrayLists
          when {
            value.all { it is String } -> bundle.putStringArrayList(key, value as ArrayList<String>)
            value.all { it is Int } -> bundle.putIntegerArrayList(key, value as ArrayList<Int>)
            // Add more specific ArrayList types if needed, e.g., CharSequence
            else -> {
              // If it's an ArrayList of Parcelable or Serializable, handle it as such
              // Note: Bundle.putParcelableArrayList requires the list elements to be Parcelable
              if (value.all { it is Parcelable }) {
                bundle.putParcelableArrayList(key, value as ArrayList<out Parcelable>)
              } else if (value.all { it is Serializable }) {
                // You can put an ArrayList<Serializable> as a Serializable object
                bundle.putSerializable(key, value)
              } else {
                Log.w("BundleConversion", "Unsupported ArrayList type for key '$key'. Consider Parcelable or Serializable.")
              }
            }
          }
        }

        // Generic Arrays (can't use .isArrayOf directly for all types like ArrayList)
        is Array<*> -> {
          when {
            value.isArrayOf<String>() -> bundle.putStringArray(key, value as Array<String>)
            value.isArrayOf<Parcelable>() -> bundle.putParcelableArray(key, value as Array<Parcelable>)
            // Add more array types if needed
            else -> {
              // If it's an array of custom objects, they need to be Parcelable or Serializable
              Log.w("BundleConversion", "Unsupported Array type for key '$key'. Consider Parcelable or Serializable.")
            }
          }
        }

        // Custom objects and nested Bundles
        is Bundle -> bundle.putBundle(key, value) // For nested Bundles
        is Parcelable -> bundle.putParcelable(key, value) // For custom Parcelable objects
        is java.io.Serializable -> bundle.putSerializable(key, value) // For custom Serializable objects
        null -> bundle.putString(key, null) // Explicitly put null for String, or you can omit
        else -> {
          // Log a warning or throw an exception if the type is not supported by Bundle
          // and cannot be converted to String without losing data.
          Log.w("BundleConversion", "Unsupported type for key '$key': ${value::class.simpleName}. Value will not be added to Bundle.")
        }
      }
    }
  }
  return bundle
}
