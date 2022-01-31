# Chapter 02 객체 생성과 파괴

## 아이템 1. 생성자 대신 정적 팩터리 메서드를 고려하라
> **핵심 정리**  
> 정적 팩터리 메서드와 public 생성자는 각자 장단점이 있지만, 정적 팩터리를 사용하는 게 유리한 경우가 더 많다.

### 장점
- 정적 팩터리 메서드는 이름을 가질 수 있다.
- 정적 팩터리 메서드는 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.
- 정적 팩터리 메서드는 반환 타입의 하위 타입 객체를 반환할 수 있다.
- 정적 팩터리 메서드는 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
- 정적 팩터리 메서드를 작성하는 시점에 반환할 객체의 클래스가 존재하지 않아도 된다.

### 단점
- 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.
- 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.

### 예제
```java
// java.lang.Boolean
@Deprecated(since="9")
public Boolean(boolean value) {
	this.value = value;
}
```

```java
// java.lang.Boolean
public static final Boolean TRUE = new Boolean(true);
public static final Boolean FALSE = new Boolean(false);

public static Boolean valueOf(String s) {
	return parseBoolean(s) ? TRUE : FALSE;
}
```

정적 팩터리 메서드를 제공하는 대표적인 예는 Boolean 클래스다. Boolean.valueOf(boolean) 메서드는 새로운 객체를 생성하지 않고, 클래스 내부에 가지고 있는 static final Boolean 객체를 반환한다. 반복되는 요청에 같은 객체를 반환하는 식으로 인스턴스를 통제하는 클래스를 인스턴스 통제(instance-controlled) 클래스라 한다. 인스턴스를 통제하면 다음의 이점을 얻을 수 있다.
1. 클래스를 싱글턴으로 만들 수 있다.
2. 인스턴스화가 불가능하게 만들 수 있다.
3. 불변 값 클래스에서 동치인 인스턴스가 단 하나뿐임을 보장할 수 있다.

### 정적 팩터리 메서드 이름 짓기
- from() : 매개변수를 하나 받아서 해당 타입의 인스턴스를 반환하는 형변환 메서드
- of() : 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드
- valueOf() : from과 of의 더 자세한 버전
- instance(), getInstance() : 매개변수를 받는다면 매개변수로 명시한 인스턴스를 반환하지만, 같은 인스턴스임을 보장하지 않음
- create(), newInstance() : instance와 같지만 매번 새로운 인스턴스를 생성해 반환함을 보장함
- get{Type}() : instance와 같지만 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 씀. {Type}은 팩터리 메서드가 반환할 객체의 타입
- new{Type}() : newInstance와 같지만 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 씀. {Type}은 팩터리 메서드가 반환할 객체의 타입
- {type}() : getType과 newType의 간결한 버전

## 아이템 2. 생성자에 매개변수가 많다면 빌더를 고려하라
> **핵심 정리**  
> 생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하자. 선택적 매개변수가 많거나 같은 타입인 것들이 많다면 빌더 패턴이 더욱 유용하다. 빌더는 점층적 생성자보다 클라이언트 코드를 읽고 쓰기가 훨씬 간결하고 자바빈즈보다 훨씬 안전하다.

정적 팩터리와 생성자는 선택적 매개변수가 많을 때 적절히 대응하기 어렵다. 이럴 때 사용할 수 있는 패턴은 다음과 같다.

### 1. 점층적 생성자 패턴
```java
public NutritionFacts(int servingSize, int servings) {
	this(servingSize, servings, 0);
}

public NutritionFacts(int servingSize, int servings, int calories) {
	this(servingSize, servings, calories, 0);
}

public NutritionFacts(int servingSize, int servings, int calories, int fat) {
	this(servingSize, servings, calories, fat, 0);
}
//...
```

점층적 생성자 패턴(Telescoping constructor pattern)이란 필수 매개변수만 받는 생성자, 필수 매개변수와 선택 매개변수 1개를 받는 생성자, 선택 매개변수를 2개까지 받는 생성자, ... 형태로 선택 매개변수를 전부 다 받는 생성자까지 늘려가는 방식이다. 이 방식의 단점은 매개변수의 개수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다는 점이다. 또한, 클라이언트가 실수로 매개변수의 순서를 바꿔 건네주면 런타임에 엉뚱한 동작을 하게 될 수도 있다.

### 2. 자바빈즈 패턴

```java
public NutritionFacts() {
}

public void setServingSize(int val) {...}
public void setServings(int val) {...}
public void setCalories(int val) {...}
public void setFat(int val) {...}
//...
```

```java
NutritionFacts cocaCola = new NutritionFacts();
cocaCola.setServingSize(240);
cocaCola.setServings(8);
cocaCola.setCalories(100);
cocaCola.set...
```

자바빈즈 패턴(JavaBeans pattern)은 매개변수가 없는 생성자로 객체를 만든 후, 세터(setter) 메서드를 호출해 원하는 매개변수의 값을 설정하는 방식이다. 불행히도 자바빈즈는 심각한 단점을 지니고 있다. 자바빈즈 패턴에서는 객체 하나를 만들려면 메서드 여러 개를 호출해야 하고, 객체가 완전히 생성되기 전까지는 일관성(consistency)이 무너진 상태에 놓이게 된다. 이 문제 때문에 자바빈즈 패턴에서는 클래스를 불변으로 만들 수 없다. 

### 3. 빌더 패턴

```java
public class NutritionFacts {
	private final int servingSize;
	private final int servings;
	private final int calories;
	private final int fat;
	
	public static class Builder {
		// 필수 매개변수
		private final int servingSize;
		private final int servings;
		
		// 선택 매개변수 - 기본값으로 초기화한다.
		private int calories = 0;
		private int fat = 0;
		
		public Builder(int servingSize, int servings) {
			this.servingSize = servingSize;
			this.servings = servings;
		}
		
		public Builder calories(int val) {
			calories = val;
			return this;
		}
		
		public Builder fat(int val) {
			fat = val;
			return this;
		}
		
		public NutritionFacts build() {
			return new NutritionFacts(this);
		}
	}
	
	private NutritionFacts(Builder builder) {
		servingSize = builder.servingSize;
		servings = builder.servings;
		calories = builder.calories;
		fat = builder.fat;
	}
}
```

```java
NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
	.calories(100)
	.fat(5)
	.build();
```

빌더 패턴(Builder pattern)은 점층적 생성자 패턴의 안전성과 자바빈즈 패턴의 가독성을 모두 가지고 있다. 빌더의 세터 메서드는 빌더 자신을 반환하기 때문에 연쇄적으로 호출할 수 있다. 이런 방식을 메서드 호출이 흐르듯 연결된다는 뜻으로 플루언트 API(fluent API) 혹은 메서드 연쇄(method chaining)라 한다.

### 단점
1. 객체를 만들려면 빌더부터 만들어야 한다.
2. 점층적 생성자 패턴보다는 코드가 장황해서 매개변수가 4개 이상은 되어야 값어치를 한다.

## 아이템 3. PRIVATE 생성자나 열거 타입으로 싱글턴임을 보증하라

싱글턴(singleton)이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다.

### 싱글턴을 만드는 방법

```java
public class Elvis {
	public static final Elvis INSTANCE = new Elvis();
	private Elvis() {...}
}
```

private 생성자는 public static final Elvis INSTANCE가 초기화될 때 딱 한 번만 호출된다. public이나 protected 생성자가 없으므로 초기화될 때 만들어진 인스턴스가 전체 시스템에서 하나뿐임이 보장된다.

```java
public class Elvis {
	private static final Elvis INSTANCE = new Elvis();
	private Elvis() {...}
	public static Elvis getInstance() { return INSTANCE; }
}
```

정적 팩터리 메서드를 public static 멤버로 제공한다. Elvis.getInstance는 항상 같은 객체의 참조를 반환한다. 

```java
public enum Elvis {
	INSTANCE;
}
```

대부분 상황에서는 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은 방법이다. 

## 아이템 4. 인스턴스화를 막으려거든 PRIVATE 생성자를 사용하라

```java
public class UtilityClass {
	// 인스턴스화 방지용
	private UtilityClass() {
		throw new AssertionError(); // 꼭 AssertionError를 던질 필요는 없다.
	}
	//...
}
```

java.util.Collections처럼 정적 멤버만 담은 유틸리티 클래스는 인스턴스로 만들어 쓰려고 설계한 게 아니다. 하지만 생성자를 명시하지 않으면 컴파일러가 자동으로 기본 생성자를 만들어준다. 이 경우 매개변수를 받지 않는 public 생성자가 만들어지며, 의도치않게 클래스가 인스턴스화할 수 있게 되버린다. 컴파일러가 기본 생성자를 만드는 경우는 명시된 생성자가 없을 때뿐이다. 따라서 private 생성자를 추가하면 클래스의 인스턴스화를 막을 수 있다.  

이 방식은 상속을 불가능하게 하는 효과도 있다. 모든 생성자는 명시적이든 묵시적이든 상위 클래스의 생성자를 호출하게 되는데, 이를 private으로 선언했으니 하위 클래스가 상위 클래스의 생성자에 접근할 길이 막혀버린다.  

## 아이템 5. 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라
> **핵심 정리**  
> 클래스가 내부적으로 하나 이상의 자원에 의존하고, 그 자원이 클래스 동작에 영향을 준다면 싱글턴과 정적 유틸리티 클래스는 사용하지 않는 것이 좋다. 대신 필요한 자원을 생성자/정적 팩터리/빌더에 넘겨주자. 의존 객체 주입이라 하는 이 기법은 클래스의 유연성, 재사용성, 테스트 용이성을 개선해준다.

```java
import java.util.Objects;

public class SpellChecker {
	private final Lexicon dictionary; // 자원

	public SpellChecker(Lexicon dictionary) { // 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨준다. - 의존 객체 주입
		this.dictionary = Objects.requireNonNull(dictionary);
	}
	
	public boolean isValid(String word) {...}
}
```

사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다. 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 의존 객체 주입 방식을 사용해야 한다.

## 아이템 6. 불필요한 객체 생성을 피하라


```java
String s = new String("apple");
String s = "apple";
```

똑같은 기능의 객체를 매번 생성하기보다는 객체 하나를 재사용하는 편이 나을 때가 많다. 재사용은 빠르고 세련되다.

```java
public static final Boolean TRUE = new Boolean(true);
public static final Boolean FALSE = new Boolean(false);

public static Boolean valueOf(String s) {
	return parseBoolean(s) ? TRUE : FALSE;
}
```

생성자 대신 정적 팩터리 메서드를 제공하는 불변 클래스에서는 정적 팩터리 메서드를 사용해 불필요한 객체 생성을 피할 수 있다.

```java
public class RomanNumerals {
	private static final Pattern ROMAN = Pattern.compile(
		"^(?=.)M*(C[MD]|D?C{0,3})" + "(X[CL]|L?X{0,3})(I[XV]|v?I{0,3})$"
	);
	static boolean isRomanNumeral(String s) {
		return ROMAN.matcher(s).matches();
	}
}
```

생성 비용이 아주 비싼 객체도 있다. 이런 비싼 객체가 반복해서 필요하다면 캐싱하여 재사용하길 권한다. String.matches는 정규표현식으로 문자열 형태를 확인하는 가장 쉬운 방법이지만 성능이 중요한 상황에서 반복 사용하기엔 적합하지 않다. 이 메서드가 내부에서 만드는 정규표현식용 Pattern 인스턴스는 입력받은 정규표현식에 해당하는 유한 상태 머신(finite state machine)을 만들기 때문에 인스턴스 생성 비용이 높은데, 한 번 쓰고 버려져서 곧바로 가비지 컬렉션 대상이 된다. 성능을 개선하려면 불변 Pattern 인스턴스를 클래스 초기화 과정에서 직접 생성해 캐싱해두고, 나중에 isRomanNumeral 메서드가 호출될 때마다 이 인스턴스를 재사용하면 된다.

```java
private static long sum() {
	Long sum = 0L;
	for (long i = 0; i <= Integer.MAX_VALUE; i++) {
		sum += i; // long i가 Long sum에 더해질 때마다 불필요한 Long 인스턴스가 만들어진다. Long 인스턴스가 약 2^31개나 생성된다.
	}
	return sum;
}
```

또 다른 예로 오토박싱(autoboxing)이 있다. 오토박싱은 프로그래머가 기본 타입과 박싱된 기본 타입을 섞어 쓸 때 자동으로 상호 변환해주는 기술이다. 박싱된 기본 타입보다는 기본 타입을 사용하고, 의도치 않은 오토박싱이 숨어들지 않도록 주의하자.

## 아이템 7. 다 쓴 객체 참조를 해제하라
> **핵심 정리**  
> 메모리 누수는 겉으로 잘 드러나지 않아 시스템에 수년간 잠복하는 사례도 있다. 이런 종류의 문제는 예방법을 익혀두는 것이 매우 중요하다.

메모리 누수(memory leak)가 발생하는 프로그램을 오래 실행하다 보면 점차 가비지 컬렉션 활동과 메모리 사용량이 늘어나 결국 성능이 저하될 것이다. 심할 때는 디스크 페이징이나 OutOfMemoryError를 일으켜 프로그램이 예기치 않게 종료되기도 한다.  

[객체 참조](https://github.com/yoo-jaein/TIL/blob/main/Java/References.md) 하나를 살려두면 가비지 컬렉터는 그 객체뿐 아니라 그 객체가 참조하는 모든 객체와 그 객체들이 참조하는 모든 객체들을 회수해가지 못한다. 그래서 단 몇 개의 객체가 매우 많은 객체를 회수하지 못하게 할 수 있고 잠재적으로 성능에 악영향을 줄 수 있다.  

```java
public Object pop() {
	if (size == 0)
		throw new EmptyStackException();
	Object result = elements[--size];
	elements[size] = null; // 다 쓴 참조 해제
	return result;
}
```

해법은 간단하다. 해당 참조를 다 썼을 때 null 처리(참조 해제)하면 된다. 다만 객체 참조를 null 처리하는 일은 예외적인 경우여야 한다. 일반적으로 자기 메모리를 직접 관리하는 클래스라면 원소를 다 사용한 즉시 그 원소가 참조한 객체들을 다 null 처리해줘야 한다.  

캐시 역시 메모리 누수를 일으키는 주범이다. 객체 참조를 캐시에 넣고 객체를 다 쓴 뒤로도 한참을 그냥 놔두는 일을 접할 수 있다. 캐시 외부에서 키를 참조하는 동안만 엔트리가 살아있는 캐시가 필요한 상황이라면 WeakHashMap을 사용해 캐시를 만들자. 다 쓴 엔트리는 즉시 자동으로 제거될 것이다.  

캐시를 만들 때 보통은 캐시 엔트리의 유효 기간을 정확히 정의하기 어렵기 때문에 시간이 지날수록 엔트리의 가치를 떨어뜨리는 방식을 흔히 사용한다. 이런 방식에서는 쓰지 않는 엔트리를 이따끔 청소해줘야 한다.   

리스너(listener) 혹은 콜백(callback). 클라이언트가 콜백을 등록만 하고 명확히 해지하지 않는다면 뭔가 조치해주지 않는 한 콜백은 계속 쌓여갈 것이다. 이럴 때 콜백을 약한 참조(weak reference)로 저장하면 가비지 컬렉터가 즉시 수거해간다. 예를 들어 WeakHashMap에 키로 저장하면 된다.

## 아이템 8. finalizer와 cleaner 사용을 피하라
> **핵심 정리**  
> cleaner는 안전망 역할이나 중요하지 않은 네이티브 자원 회수용으로만 사용하자. 물론 이런 경우라도 불확실성과 성능 저하에 주의해야 한다.

자바는 finalizer와 cleaner라는 두 가지 객체 소멸자를 제공한다. 그 중 finalizer는 예측할 수 없고, 상황에 따라 위험할 수 있어 일반적으로 불필요하다. clenaer는 finalizer보다는 덜 위험하지만, 여전히 예측할 수 없고 느리기 때문에 일반적으로 불필요하다. 

### 단점
- finalizer와 cleaner는 즉시 수행된다는 보장이 없다. 수행 시점은 JVM 구현마다 천차만별이며 수행 시점에 의존하는 프로그램의 동작도 마찬가지다.
- finalizer와 cleaner는 수행 여부도 보장하지 않는다. 데이터베이스처럼 상태를 영구적으로 수정하는 작업에서는 절대 finalizer, cleaner를 의존해서는 안 된다.
- finalizer 동작 중 발생한 예외는 무시되며 처리할 작업이 남아도 그 순간 종료된다.
- finalizer와 cleaner는 심각한 성능 문제도 동반한다.
- finalizer를 사용한 클래스는 finalizer 공격에 노출된다.

### 해결
finalizer나 cleaner를 대신하여 AutoCloseable을 구현해주고, 클라이언트에서 인스턴스를 다 쓰고 나면 close()를 호출하도록 하자.

## 아이템 9. try-finally보다는 try-with-resources를 사용하라
> **핵심 정리**  
> 꼭 회수해야 하는 자원을 다룰 때는 try-finally 말고, try-with-resources를 사용하자. 예외는 없다.

자바 라이브러리에는 InputStream, OutputStream, Connection 등 close()를 호출해 직접 닫아줘야 하는 자원이 많다. 자원 닫기는 클라이언트가 놓치기 쉬워서 성능 문제로 이어지기도 한다. try-with-resources 버전이 짧고 읽기 수월하며 문제를 진단하기도 좋다. 이 구조를 사용하려면 해당 자원이 AutoCloseable 인터페이스를 구현해야 한다. 이미 자바 라이브러리와 서드파티 라이브러리들에서 AutoCloseable을 구현하거나 확장해뒀다.

### try-finally
```java
BufferedReader br = new BufferedReader(new FileReader(path));
try {
	return br.readLine();
} finally {
	br.close();
}
```

```java
InputStream in = new FileInputStream(src);
try {
	OutputStream out = new FileOutputStream(dst);
	try {
		byte[] buf = new byte[BUFFER_SIZE];
		int n;
		while ((n = in.read(buf)) >= 0)
			out.write(buf, 0, n);
	} finally {
		out.close();
	}
} finally {
	in.close();
}
```

### try-with-resources
```java
try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	return br.readLine();
} catch (IOException e) {
	return defaultVal;
}
```

```java
try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dst)) {
	byte[] buf = new byte[BUFFER_SIZE];
	int n;
	while ((n = in.read(buf)) >= 0)
		out.write(buf, 0, n);
}
```
