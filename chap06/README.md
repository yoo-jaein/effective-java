# Chapter 06 열거 타입과 애너테이션

## 아이템 34. int 상수 대신 열거 타입을 사용하라 
> **핵심 정리**  
> 열거 타입은 정수 상수보다 뛰어나다. 하나의 메서드가 상수별로 다르게 동작해야 한다면 switch 문 대신 상수별 메서드 구현을 사용하자. 열거 타입 상수의 일부가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하자.

자바에는 특수한 목적의 참조 타입이 두 가지가 있다. 하나는 ```클래스```의 일종인 ```열거 타입(enum)```이고, 다른 하나는 ```인터페이스```의 일종인 ```애너테이션(annotation)```이다. ```열거 타입```은 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입이다. 자바가 ```열거 타입```을 지원하기 전에는 다음 코드처럼 ```정수 열거 패턴``` 혹은 ```문자열 열거 패턴```을 사용했다.

### 정수 열거 패턴과 문자열 열거 패턴
```java
public class Fruit {
	public static final int APPLE_FUJI = 0;
	public static final int APPLE_PIPPIN = 1;
	public static final int APPLE_GRANNY_SMITH = 2;

	public static final int ORANGE_NAVEL = 0;
	public static final int ORANGE_TEMPLE = 1;
	public static final int ORANGE_BLOOD = 2;
}
```

```정수 열거 패턴(int enum pattern)```의 정수 상수는 문자열로 출력하거나 디버거로 살펴보면 의미가 아닌 단순한 숫자로만 보여서 썩 도움이 되지 않는다. 타입 안전성을 보장할 수도 없다. ORANGE 상수를 건네야 할 메서드에 APPLE 상수를 넘기더라도 컴파일러는 아무런 경고 메시지를 출력하지 않는다. 정수 대신 문자열 상수를 사용하는 패턴이 ```문자열 열거 패턴(string enum pattern)```이다. ```문자열 열거 패턴```은 문자열 값을 하드코딩해야 하기 때문에 더욱 나쁜 패턴이다.  

### 열거 타입
```java
public enum Apple {
	FUJI, PIPPIN, GRANNY_SMITH
}
```
```java
public enum Orange {
	NAVEL, TEMPLE, BLOOD
}
```
```java
public enum Planet {
	MERCURY(3.302e+23, 2.439e6),
	VENUS(4.869e+24, 6.052e6);
	
	private final double mass;
	private final double radius;
	private final double surfaceGravity;
	
	private static final double G = 6.67300E-11;
	
	Planet(double mass, double radius) {
		this.mass = mass;
		this.radius = radius;
		this.surfaceGravity = G * mass / (radius * radius);
	}
	
	public double getMass() { return this.mass; }
	public double getRadius() { return this.radius; }
	public double getSurfaceGravity() { return this.surfaceGravity; }
	
	public double surfaceWeight(double mass) {
		return mass * surfaceGravity; // F = ma
	}
}
```

```열거 타입(enum type)```은 다음의 특징을 가진다.
- ```열거 타입```은 클래스이며 상수 하나당 자신의 인스턴스를 하나씩 만들어 ```public static final 필드```로 공개한다.
- ```열거 타입```은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로 사실상 ```final```이다.
- 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나씩만 존재함이 보장된다. 따라서 ```열거 타입```은 ```인스턴스 통제```되며 ```싱글턴```을 일반화한 형태라고 볼 수 있다.
- ```열거 타입```은 ```컴파일타임 타입 안정성```을 제공한다. Apple 열거 타입을 매개변수로 받는 메서드를 선언했다면, 타입이 다른 변수를 넘기려 할 때 컴파일 오류가 난다.
- ```열거 타입```의 ```toString``` 메서드는 출력하기에 적합한 문자열을 보여준다.
- ```열거 타입```에는 임의의 메서드나 필드를 추가할 수 있고 인터페이스를 구현하게 할 수도 있다.
- 널리 쓰이는 열거 타입은 톱레벨 클래스로 만들고, 특정 톱레벨 클래스에서만 쓰인다면 해당 클래스의 ```멤버 클래스```로 만들자.
- ```열거 타입```은 근본적으로 불변이라 모든 필드는 ```final```이어야 한다.

### switch 문

```java
public enum Operation {
	PLUS, MINUS;
	
	public double apply(double x, double y) {
		switch(this) {
			case PLUS: return x + y;
			case MINUS: return x - y;
		}
		throw new AssertionError("알 수 없는 연산: " + this);
	}
}
```

열거 타입의 상수별로 다르게 동작하는 코드를 구현하고 싶다면 switch 문을 이용해 상수의 값에 따라 분기하는 방법을 시도할 수 있다. 그러나 switch 문을 쓰면 깨지기 쉬운 코드가 발생한다. 마지막 throw 문은 실제로는 도달할 수 없지만 기술적으로는 도달할 수 있기 때문에 생략하면 컴파일이 되지 않는다. (Missing return statement) 또한, 새로운 상수를 추가하면 해당 case문도 추가해야 한다. 추가하지 않으면 컴파일은 되지만 새로 추가한 연산을 수행하려 할 때 "알 수 없는 연산"이라는 런타임 오류가 발생한다.

### 상수별 메서드 구현
```java
public enum Operation {
	PLUS {
		public double apply(double x, double y) {
			return x + y;
		}
	},
	MINUS {
		public double apply(double x, double y) {
			return x - y;
		}
	};
	
	public abstract double apply(double x, double y);
}
```
switch 문으로 분기 처리하는 방법 대신 ```상수별 메서드 구현(constant-specific method implementation)```방법을 사용해보자. 열거 타입에 ```추상 메서드```를 선언하고 ```각 상수별 클래스 몸체(constant-specific class body)```에 맞게 재정의하는 방법이다. 새로운 상수를 추가할 때 apply가 ```추상 메서드```이므로 재정의하지 않았다면 컴파일 오류로 알려준다.

### 전략 열거 타입 패턴
```java
public enum PayrollDay {
	MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY),
	THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),
	SATURDAY(WEEKEND), SUNDAY(WEEKEND);
	
	private final PayType payType;
	
	PayrollDay(PayType payType) { this.payType = payType; }
	
	int pay(int minutesWorked, int payRate) {
		return payType.pay(minutesWorked, payRate);
	}
	
	// 전략 열거 타입
	private enum PayType {
		WEEKDAY {
			int overtimePay(int minsWorked, int payRate) {
				return minsWorked <= MINS_PER_SHIFT ? 0 : (minsWorked - MINS_PER_SHIFT) * payRate / 2; 
			}
		},
		WEEKEND {
			int overtimePay(int minsWorked, int payRate) {
				return minsWorked * payRate / 2;
			}
		};
		
		abstract int overtimePay(int mins, int payRate);

		private static final int MINS_PER_SHIFT = 8 * 60;
		
		int pay(int minsWorked, int payRate) {
			int basePay = minsWorked * payRate;
			return basePay + overtimePay(minsWorked, payRate);
		}
	}
}
```
PayrollPay 열거 타입은 잔업수당 계산을 private 중첩 열거 타입 PayType에 위임했다. 

---

## 아이템 35. ordinal 메서드 대신 인스턴스 필드를 사용하라
모든 열거 타입은 해당 상수가 그 열거 타입에서 몇 번째 위치인지를 반환하는 ordinal 메서드를 제공한다. 이 메서드는 EnumSet, EnumMap 같이 열거 타입 기반의 범용 자료구조에 쓸 목적으로 설계되었기 때문에 이런 용도가 아니라면 ordinal 메서드는 절대 사용하지 말자.

---

## 아이템 36. 비트 필드 대신 EnumSet을 사용하라
> **핵심 정리**  
> EnumSet 클래스는 비트 필드 수준의 명료함과 성능을 제공한다. EnumSet의 유일한 단점은 불변 EnumSet을 만들 수 없다는 것이다.

### 비트 필드
열거한 값들이 주로 단독이 아닌 집합으로 사용될 경우, 예전에는 각 상수에 서로 다른 2의 거듭제곱 값을 할당한 정수 열거 패턴을 사용해왔다. 
```java
public class Text {
	public static final int STYLE_BOLD = 1 << 0; // 1
	public static final int STYLE_ITALIC = 1 << 1; // 2
	public static final int STYLE_UNDERLINE = 1 << 2; // 4
	public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8
	
	public void applyStyles(int styles) {...} // styles는 0개 이상의 STYLE_ 상수를 비트별 OR한 값
}
```

다음과 같은 식으로 비트별 OR를 사용해 여러 상수를 하나의 집합으로 모을 수 있으며, 이렇게 만들어진 집합을 ```비트 필드(bit field)```라 한다.
```java
text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
```

```비트 필드```를 사용하면 비트별 연산을 사용해 합집합과 교집합 같은 집합 연산을 효율적으로 수행할 수 있다. 그러나 ```비트 필드```는 ```정수 열거 상수```의 단점을 그대로 지니며, 추가로 다음과 같은 문제를 안고 있다.

- 비트 필드 값이 그대로 출력되면 해석하기 어렵다.
- 최대 몇 비트가 필요한지를 API 작성 시 미리 예측하여 적절한 타입을 선택해야 한다. API를 수정하지 않고는 비트 수를 더 늘릴 수 없다.

### EnumSet
```java
public class Text {
	public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }
	
	public void applyStyles(Set<Style> styles) {...} // 이왕이면 인터페이스 파라미터
}
```

```java
text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
```

java.util.```EnumSet``` 클래스는 열거 타입 상수의 값으로 구성된 집합을 효과적으로 표현해준다. ```Set``` 인터페이스를 완벽히 구현하며 타입 세이프하다. 집합 생성 등 다양한 정적 팩터리를 제공한다. 모든 클라이언트가 ```EnumSet```을 건네리라 짐작되는 상황이라도 이왕이면 ```인터페이스```로 받는 게 좋은 습관이다. 

---

## 아이템 37. ordinal 인덱싱 대신 EnumMap을 사용하라
> **핵심 정리**  
> 배열의 인덱스를 얻기 위해 ordinal을 쓰는 것은 일반적으로 좋지 않으니 EnumMap을 사용하자.

```java
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);

for (Plant.LifeCycle lc : Plant.LifeCycle.values())
	plantsByLifeCycle.put(lc, new HashSet<>());

for (Plant p : garden)
	plantsByLifeCycle.get(p.lifeCycle).add(p);

System.out.println(plantsByLifeCycle);
```

여기서 배열은 실질적으로 열거 타입 상수를 값으로 매핑하는 일을 한다. 그러니 열거 타입을 키로 사용하는 맵인 ```EnumMap```을 사용하는 것이 좋다. ```EnumMap```은 내부에서 배열을 사용하기 때문에 성능이 배열과 비등하며 내부 구현 방식을 숨겨서 타입 안전성까지 얻어냈다.

```java
System.out.println(Arrays.stream(garden)
	.collect(groupingBy(p -> p.lifeCycle)));
```
스트림을 사용해 맵을 관리하면 코드를 더 줄일 수 있다. 다만 이 코드는 ```EnumMap```이 아닌 고유한 맵 구현체를 사용했기 때문에 ```EnumMap``` 방법에서 얻은 공간과 성능 이점이 사라진다.

```java
System.out.println(Arrays.stream(garden)
	.collect(groupingBy(p -> p.lifeCycle,
		() -> new EnumMap<>(LifeCycle.class), toSet())));
```
매개변수 3개짜리 Collectors.groupingBy 메서드는 mapFactory 매개변수에 원하는 맵 구현체를 명시해 호출할 수 있다. 여기에 EnumMap을 넘겨줘서 성능 최적화시킬 수 있다.

---

## 아이템 38. 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라
> **핵심 정리**  
> 열거 타입 자체는 확장할 수 없지만 인터페이스와 그 인터페이스를 구현하는 기본 열거 타입을 함께 사용해 확장과 같은 효과를 낼 수 있다.

열거 타입은 다른 열거 타입을 확장할 수 없다. 대부분의 상황에서 열거 타입을 확장한다는 것은 좋지 않은 생각이지만 예제 계산기의 연산 코드에는 확장해볼만 하다. 기본 아이디어는 열거 타입이 임의의 인터페이스를 구현할 수 있다는 사실을 이용하는 것이다. 연산 코드용 인터페이스를 정의하고 열거 타입이 이 인터페이스를 구현하게 하면 된다. 이때 열거 타입이 그 인터페이스의 표준 구현체 역할을 한다.

```java
public interface Operation {
	double apply(double x, double y);
}

public enum BasicOperation implements Operation {
	PLUS("+") {
		@Override
		public double apply(double x, double y) {
			return x + y;
		}
	},
	MINUS("-") {
		@Override
		public double apply(double x, double y) {
			return x - y;
		}
	};

	private final String symbol;

	BasicOperation(String symbol) {
		this.symbol = symbol;
	}
}

public enum ExtendedOperation implements Operation {
	EXP("^") {
		@Override
		public double apply(double x, double y) {
			return Math.pow(x, y);
		}
	},
	REMAINDER("%") {
		@Override
		public double apply(double x, double y) {
			return x % y;
		}
	};
	
	private final String symbol;
	
	ExtendedOperation(String symbol) { 
		this.symbol = symbol;
	}
}
```

열거 타입인 BasicOperation은 확장할 수 없지만 인터페이스인 Operation은 확장할 수 있고, 이 인터페이스를 연산의 타입으로 사용하면 된다. Operation을 확장한 ExtendedOperation 열거 타입은 BasicOperation을 대체할 수 있다. 이 방식의 작은 단점은 열거 타입끼리 구현을 상속할 수 없다는 점이다. 이 문제는 중복되는 코드나 공유하는 기능이 많다면 그 부분을 별도의 도우미 클래스로 분리하는 방식으로 개선할 수 있다.

---

## 아이템 39. 명명 패턴보다 애너테이션을 사용하라

### JUnit 3의 명명 패턴
JUnit은 버전 3까지 테스트 메서드 이름을 test로 시작하게끔 했다. 전통적으로 도구나 프레임워크가 특별히 다뤄야 할 프로그램 요소에는 이런 명명 패턴을 적용해왔다. 효과적인 방법이지만 단점도 크다. 오타가 나면 안 된다. 실수로 tsetSafetyOverride로 지으면 JUnit 3은 이 메서드를 무시하고 지나친다. 또한, 올바른 요소에서만 사용되리라는 보장을 할 수 없다. 개발자가 메서드가 아닌 클래스의 이름을 TestSafetyMechanism으로 지어 JUnit에 던져줬다고 해보자. 개발자는 클래스에 정의된 테스트 메서드들을 수행해주길 기대하겠지만 JUnit은 클래스를 무시하고 지나친다.

### JUnit 4의 애너테이션
```java
/**
 * 테스트 메서드임을 선언하는 애너테이션
 * 매개변수 없는 정적 메서드 전용이다.
 */
@Retention(RetentionPolicy.RUNTIME) // @Test가 런타임에도 유지되어야 한다는 표시
@Target(ElementType.METHOD) // @Test가 반드시 메서드 선언에서만 사용되어야 한다는 표시
public @interface Test {
}
```
JUnit은 버전 4부터 ```@Test``` 애너테이션을 도입했다. @Retention과 @Target처럼 애너테이션 선언에 다는 애너테이션을 ```메타애너테이션(meta-annotation)```이라 한다. '매개변수 없는 정적 메서드 전용'이라는 제약을 강제하려면 javax.annotation.processing API 문서를 참고하여 적절한 애너테이션 처리기를 직접 구현해야 한다.  

---

## 아이템 40. @Override 애너테이션을 일관되게 사용하라
> **핵심 정리**  
> 재정의한 모든 메서드에 @Override 애너테이션을 달자.

```@Override```는 메서드 선언에만 달 수 있으며, 이 애너테이션이 달렸다는 것은 상위 타입의 메서드를 재정의했음을 뜻한다. 구체 클래스에서 상위 클래스의 추상 메서드를 재정의한 경우엔 ```@Override```를 달지 않아도 되지만 일관되게 다는 것이 좋다.

---

## 아이템 41. 정의하려는 것이 타입이라면 마커 인터페이스를 사용하라
> **핵심 정리**  
> 마커 인터페이스와 마커 애너테이션은 각자의 쓰임이 있다. 새로 추가하는 메서드 없이 단지 타입 정의가 목적이라면 마커 인터페이스를 선택하자. 클래스나 인터페이스 외의 프로그램 요소에 마킹해야 하거나, 애너테이션을 적극 활용하는 프레임워크의 일부로 그 마커를 편입시키고자 한다면 마커 애너테이션이 올바른 선택이다.

아무 메서드도 담고 있지 않고, 단지 자신을 구현하는 클래스가 특정 속성을 가짐을 표시해주는 인터페이스를 ```마커 인터페이스(marker interface)```라 한다. Serializable 인터페이스가 좋은 예다. 마커를 클래스나 인터페이스에 적용해야 한다면 "이 마킹이 된 객체를 매개변수로 받는 메서드를 작성할 일이 있을까?"라고 자문해보자. 답이 "그렇다"이면 ```마커 인터페이스```를 써야 한다. 이렇게 하면 그 ```마커 인터페이스```를 해당 메서드의 매개변수 타입으로 사용하여 컴파일타임에 오류를 잡아낼 수 있다. 
