# Worse arrays

Why use arrays if you can create objects with number of fields == array size?

# Usage

```java
Array<String> myArray = Array.allocate(10);

myArray.size() == 10

myArray.get(0) == null

myArray.set(0, "asdf");
myArray.get(0) == "asdf"

myArray.get(-1)
java.lang.ArrayIndexOutOfBoundsException
```

## Note

All array classes are lazily generated at runtime using ASM. The generated classes
have the name `<SIZE>SizedArray` eg `0SizedArray`, `10SizedArray`

The classes have all fields of type Object, and generic signature is ignored, so the
following code won't throw:
```java
Array<String> strings = Array.allocate(10);

Array<Integer> ints = Array.allocate(10);

if(ints.getClass() != strings.getClass()) {
    throw new AssertionError();
}
```