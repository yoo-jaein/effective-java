# Chapter 05 제네릭

## 아이템 26. 로 타입은 사용하지 말라
> **핵심 정리**  
> 로 타입은 제네릭이 도입되기 이전 코드와의 호환성을 위해 제공될 뿐이다. 로타입을 사용하면 런타임에 예외가 일어날 수 있으니 사용하면 안 된다. Set<Object\>와 Set<?\>는 안전하지만, 로 타입인 Set은 안전하지 않다.

### 제네릭
```제네릭(generic)```은 자바 5부터 사용할 수 있다. ```제네릭```을 지원하기 전에는 컬렉션에서 객체를 꺼낼 때마다 형변환을 해야 했다. 만약 누군가 실수로 엉뚱한 타입의 객체를 넣어두면 런타임에 형변환 오류가 나곤 했다. 반면, ```제네릭```을 사용하면 컬렉션이 담을 수 있는 타입을 컴파일러에게 알려주게 되고, 컴파일러가 알아서 형변환 코드를 추가하면서 엉뚱한 타입의 객체를 컴파일 과정에서 차단할 수 있다.  

클래스와 인터페이스 선언에 ```타입 매개변수(type parameter)```가 쓰이면, 이를 ```제네릭 클래스``` 혹은 ```제네릭 인터페이스```라 한다. 이 둘을 통틀어 ```제네릭 타입(generic type)```이라 한다. 각각의 제네릭 타입은 일련의 ```매개변수화 타입(parameterized type)```을 정의한다. 

```java
List<E> // Raw use of parameterized class 'List'
List<String>
```

예를 들어 ```List<String>```은 원소의 타입이 String인 리스트를 뜻하는 ```매개변수화 타입```이다. List<E\>의 ```E```는 ```정규 타입 매개변수(formal type parameter)```이고, List<String\>의 ```String```은 E의 ```실제 타입 매개변수(actual type paramter)```다.

```java
List
```

제네릭 타입을 하나 정의하면 그에 딸린 ```로 타입(raw type)```도 함께 정의된다. ```로 타입```이란 제네릭 타입에서 타입 매개변수를 사용하지 않을 때를 말한다. List<E\>의 로 타입은 ```List```다.

### 제네릭 이전
```java
private final Collection stamps = ...; // Stamp 인스턴스만 취급하는 컬렉션
stamps.add(new Coin(...)); // unchecked call 경고 메시지를 보여주지만 정상적으로 컴파일됨

for (Iterator i = stamps.iterator(); i.hasNext(); ) {
	Stamp stamp = (Stamp) i.next(); // 이 때 런타임에 ClassCastException 발생
	stamp.cancel();
}
```

### 제네릭 코드
```java
private final Collection<Stamp> stamps = ...; // 컴파일러가 stamps에는 Stamp 인스턴스만 넣어야 함을 인지하게 됨
stamps.add(new Coin(...)); // 컴파일 에러 발생! Required type: Integer, Provided: Float
```

### List<Object\>와 List<?\>
List<Object\>는 모든 타입을 허용한다는 의사를 컴파일러에게 전달한다. List<?\>는 ```비한정적 와일드카드 타입(unbounded wildcard type)``` 물음표(?)를 사용하여 제네릭 타입을 사용하고 싶지만 실제 타입 매개변수가 무엇인지 신경 쓰고 싶지 않다는 것을 표현한다. List<?\>는 어떤 타입이라도 담을 수 있는 가장 범용적인 매개변수화 List 타입이다.

---

## 아이템 27. 비검사 경고를 제거하라
> **핵심 정리**  
> 모든 비검사 경고는 런타임에 ClassCastException을 일으킬 수 있는 잠재적 가능성을 뜻하기 때문에 가능한 모두 제거해야 한다. 경고를 없앨 수 없다면, 그 코드가 타입 세이프함을 증명하고 가능한 한 범위를 좁혀 @SuppressWarnings("unchecked") 애너테이션으로 경고를 숨겨라.

제네릭을 사용하면 많은 컴파일러 경고를 보게 된다. 비검사(unchecked) 형변환 경고, 비검사 메서드 호출 경고, 비검사 변환 경고 등이다. 할 수 있는 한 모든 ```비검사 경고```를 제거해야 한다. 모두 제거한다면 그 코드는 타입 안정성이 보장된다. 경고를 제거할 수는 없지만 타입 세이프하다고 확신할 수 있다면 ```@SuppressWarnings("unchecked")``` 애너테이션을 달아 경고를 숨기자. 이 애너테이션은 개별 지역변수 선언부터 클래스 전체까지 어떤 선언에도 달 수 있다. 자칫 심각한 경고를 놓칠 수 있으므로 가능한 한 좁은 범위에 적용해야 한다. 

---

## 아이템 28. 배열보다는 리스트를 사용하라
> **핵심 정리**  
> 배열은 공변이고 실체화되는 반면, 제네릭은 불공변이고 타입 정보가 소거된다. 따라서 배열은 런타임에 타입 세이프하지만 컴파일에는 그렇지 않다. 제네릭은 컴파일타임에 타입 세이프하지만 런타임에는 그렇지 않다. 그래서 둘을 섞어 쓰기란 쉽지 않다. 둘을 섞어 쓰다가 컴파일 오류나 경고를 만나면, 배열을 리스트로 대체하는 방법을 적용해보자.

### 배열과 제네릭 타입의 차이
배열과 제네릭 타입에는 중요한 차이가 있다.
1. 배열은 ```공변(covariant)```이지만 제네릭 타입은 ```불공변(invariant)```이다.
2. 배열은 ```실체화(reify)```되지만 제네릭 타입은 타입 정보가 런타임에 ```소거(erasure)```된다.

#### 1. 공변과 불공변
배열은 ```공변```이지만 제네릭 타입은 ```불공변```이라는 것이 무슨 뜻일까? 만약 Integer가 Number의 하위 타입이라고 했을 때, Integer[]는 Number[]가 되며(is-a 관계) Integer[]를 Number[]에 할당하거나 전달할 수 있다. 그러나 List<Number\>와 List<Integer\>의 관계에서 List<Integer\>를 List<Number\>에 넘겨줄 수 없다.  

그 이유는 ```공변``` 관계가 제네릭이 제공하는 타입 안전성을 깨트릴 수 있기 때문이다. List<Integer\>를 List<Number\>에 할당한다고 가정해보자.  

```java
List<Integer> integerList = new ArrayList<Integer>();
List<Number> numberList = integerList; // illegal! Required type: List<Number>, Provided: List<Integer>
numberList.add(new Float(3.1415));
```

numberList는 List<Number\>이기 때문에 여기에 Float를 추가하는 것은 합법적인 것처럼 보인다. 그러나 numberList에는 integerList가 할당되어 있기 때문에 integerList의 정의에 암시된 Integer 타입 세이프 약속이 깨진다. 이것이 제네릭 타입이 ```공변```이 될 수 없는 이유다. 

#### 2. 실체화와 소거
배열은 ```실체화```되지만 제네릭 타입은 타입 정보가 런타임에 ```소거```된다. 배열은 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 체크한다. 그래서 Long 배열에 String을 넣으려 하면 런타임에 ArrayStoreException이 발생한다. 반면, 제네릭은 타입 정보가 런타임에는 ```소거```된다. 원소 타입을 컴파일타임에만 검사하며 런타임에는 알 수 없다는 뜻이다. 

---
## 아이템 29. 이왕이면 제네릭 타입으로 만들라
> **핵심 정리**  
> 클라이언트에서 직접 형변환해야 하는 타입보다 제네릭 타입이 더 안전하고 쓰기 편하다.

### 일반 클래스
```java
import java.util.EmptyStackException;

public class Stack {
	private Object[] elements;
	private int size = 0;
	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	public Stack() {
		elements = new Object[DEFAULT_INITIAL_CAPACITY];
	}

	public void push(Object e) {...}

	public Object pop() {
		if (size == 0)
			throw new EmptyStackException();
		Object result = elements[--size];
		elements[size] = null; // 다 쓴 참조 해제
		return result;
	}
}
```

### 제네릭 클래스

```java
import java.util.EmptyStackException;

public class Stack<E> {
	private E[] elements;
	private int size = 0;
	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	public Stack() {
		elements = new E[DEFAULT_INITIAL_CAPACITY]; // 컴파일 에러 발생! Type parameter 'E' cannot be instantiated directly
	}

	public void push(E e) {...}

	public E pop() {
		if (size == 0)
			throw new EmptyStackException();
		E result = elements[--size];
		elements[size] = null;
		return result;
	}
}
```

E와 같은 ```실체화 불가 타입```으로는 배열을 만들 수 없다. 적절한 해결책은 두 가지다. 첫 번째는 Object 배열을 생성한 다음 제네릭 배열 E[]로 형변환하는 방법이다. 두 번째는 elements 필드의 타입을 E[]에서 Object[]로 바꾸는 것이다. 비검사 경고를 지울 수 없는 경우 형변환이 안전함을 증명한 뒤 ```@SuppressWarnings("unchecked")``` 애너테이션을 사용한다.

### 힙 오염
위의 첫 번째 해결책을 사용하면 E가 Object가 아닌 한 배열의 런타임 타입이 컴파일타임 타입과 달라 ```힙 오염(heap pollution)```을 일으킨다.

---
## 아이템 30. 이왕이면 제네릭 메서드로 만들라
> **핵심 정리**  
> 클라이언트에서 입력 매개변수와 반환값을 명시적으로 형변환해야 하는 메서드보다 제네릭 메서드가 더 안전하고 쓰기 편하다.

### 제네릭 메서드
메서드도 제네릭으로 만들 수 있다. Collections의 알고리즘 메서드(binarySearch, sort 등)는 모두 제네릭이다.

```java
public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key) {
	if (list instanceof RandomAccess || list.size()<BINARYSEARCH_THRESHOLD)
		return Collections.indexedBinarySearch(list, key);
	else
		return Collections.iteratorBinarySearch(list, key);
}
```

```타입 매개변수 목록```은 메서드의 ```제한자```와 ```반환 타입``` 사이에 온다. binarySearch 메서드의 ```타입 매개변수 목록```은 ```<T>```이고 ```반환 타입```은 ```int```이다.

### 제네릭 싱글턴 팩터리
```java
public static <T> Comparator<T> reverseOrder() {
	return (Comparator<T>) ReverseComparator.REVERSE_ORDER;
}
```

```java
public static final <T> Set<T> emptySet() {
	return (Set<T>) EMPTY_SET;
}
```
Collections.reverseOrder나 Collections.emptySet같이 요청한 ```타입 매개변수```에 맞게 그 객체의 타입을 바꿔주는 ```정적 팩터리```를 ```제네릭 싱글턴 팩터리```라 한다. 

---
## 아이템 31. 한정적 와일드카드를 사용해 API 유연성을 높이라
> **핵심 정리**  
> 조금 복잡하더라도 와일드카드 타입을 적용하면 API가 훨씬 유연해진다. 널리 쓰일 라이브러리를 작성한다면 와일드카드 타입을 적절히 사용해줘야 한다.

매개변수화 타입은 불공변(invariant)이다. 하지만 때론 불공변 방식보다 유연한 무언가가 필요하다. 자바는 한정적 와일드카드 타입이라는 특별한 매개변수화 타입을 지원한다.  

```java
public void pushAll(Iterable<? extends E> src) {
	for (E e : src)
		push(e);
}
```

위 코드에서 pushAll의 입력 매개변수 타입 Iterable<? extends E>는 'E 혹은 E의 하위 타입의 Iterable'이라는 뜻이다. (모든 타입은 자기 자신의 하위 타입이다.)

```java
public void popAll(Collection<? super E> dst) {
	while (!isEmpty())
		dst.add(pop());
}
```

위 코드에서 popAll의 입력 매개변수 타입 Collection<? super E>는 'E 혹은 E의 상위 타입의 Collection'이어야 한다. (모든 타입은 자기 자신의 상위 타입이다.)  

유연성을 극대화하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라. 다음 공식을 외우자.  
- PECS : producer-extends, consumer-super

즉, 매개변수화 타입 T가 생산자라면 <? extends T>를 사용하고, 소비자라면 <? super T>를 사용하라. 주의해야 할 점은, 반환 타입에는 한정적 와일드카드 타입을 사용해서는 안 된다는 것이다. 반환 타입에 사용하면 클라이언트 코드에서도 와일드카드 타입을 써야하므로 좋지 않다.  

---
## 아이템 32. 제네릭과 가변인수를 함께 쓸 때는 신중하라
> **핵심 정리**  
> 제네릭과 가변인수는 궁합이 좋지 않다. 가변인수 기능은 배열을 노출하고, 배열과 제네릭의 타입 규칙이 서로 다르기 때문이다. 

### 제네릭과 가변인수 varargs
가변인수는 메서드에 넘기는 인수의 개수를 클라이언트가 조절할 수 있게 해준다. 가변인수 메서드를 호출하면 가변인수를 담기 위한 배열이 자동으로 하나 만들어진다. 배열과 제네릭은 타입 규칙이 다르므로 varargs를 제네릭과 혼용했을 때 알기 어려운 컴파일 경고가 발생할 수 있다. 메서드를 선언할 때 실체화 불가 타입으로 varargs 매개변수를 선언하면 컴파일러가 경고를 보낸다. 

```java
public void dangerous(List<String>... stringLists) { // 경고! Possible heap pollution from parameterized vararg type 
	List<Integer> intList = List.of(42);
	Object[] objects = stringLists;
	objects[0] = intList; // 힙 오염 발생
	String s = stringLists[0].get(0); // ClassCastException 발생
}
```

매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다. 다른 타입 객체를 참조하는 상황에서 컴파일러가 자동 생성한 형변환은 실패할 수 있다. 여기서 제네릭 시스템이 약속한 타입 안정성의 근간이 흔들려버린다. 마지막 줄 String s = stringsList[0].get(0);에 컴파일러가 생성한 (보이지 않는) 형변환이 숨어있다. 인수를 건네 호출하는 순간 예외가 발생한다. 이처럼 타입 안정성이 깨지니 제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.  

하지만 제네릭과 가변인수를 함께 사용하는 메서드가 실무에서 매우 유용하기 때문에 자바 라이브러리도 Arrays.asList(T... a), Collections.addAll(Collection<? super T> c, T... elements) 등 메서드를 제공한다. 이 메서드들은 dangerous 메서드와 달리 타입 세이프하다.  

### @SafeVarargs
@SafeVarargs 애너테이션은 메서드 작성자가 그 메서드가 타입 세이프함을 보장하는 장치다. 이 애너테이션이 붙어있다면 컴파일러는 그 메서드가 안전하지 않을 수 있다는 경고를 하지 않는다. 메서드가 타입 세이프함을 확인하려면 다음 조건을 검사한다.
- 가변 인수 메서드를 호출할 때 varargs 매개변수를 담는 제네릭 배열에 아무것도 덮어쓰지 않고 그 배열의 참조가 밖으로 노출되지 않는가?
- varargs 매개변수 배열이 순수하게 인수들을 전달하는 일만 하는가?
- varargs 매개변수 배열이 신뢰할 수 없는 코드에 노출되지 않았는가?

---
## 아이템 33. 타입 안전 이종 컨테이너를 고려하라
> **핵심 정리**  
> 컬렉션 API같이 일반적인 제네릭 형태에서는 한 컨테이너가 다룰 수 있는 타입 매개변수의 수가 고정되어 있다.  
