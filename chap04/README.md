# Chapter 04 클래스와 인터페이스

## 아이템 15. 클래스와 멤버의 접근 권한을 최소화하라
> **핵심 정리**  
> 꼭 필요한 것만 골라 최소한의 public API를 설계하자. 그 외에는 클래스, 인터페이스, 멤버가 의도치 않게 API로 공개되는 일이 없도록 해야 한다. public 클래스는 상수용 public static final 필드 외에는 어떠한 public 필드도 가져서는 안 된다.

### 접근 수준
- private : 멤버를 선언한 톱레벨 클래스에서만 접근 가능
- package-private : 멤버가 소속된 패키지 안의 모든 클래스에서 접근 가능. 접근 제한자를 명시하지 않았을 때 적용
- protected : package-private의 접근 범위를 포함하며, 이 멤버를 선언한 클래스의 하위 클래스에서도 접근 가능
- public : 모든 곳에서 접근 가능

### 접근 권한 최소화하기
모든 클래스와 멤버의 접근성을 최소한으로 좁혀야 한다. 그리고 public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다. 단 한 가지 예외는 해당 클래스에 꼭 필요한 구성요소로써 상수용의 public static final 필드다. 관례상 이런 상수의 이름은 대문자 알파벳으로 쓰며, 각 단어 사이에 밑줄(_)을 넣는다. 이 필드는 반드시 기본 타입 값이나 불변 객체를 참조해야 하며, 만약 가변 객체를 참조할 경우 참조된 객체 자체가 수정될 수 있는 위험이 있다.

## 아이템 16. public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라
> **핵심 정리**  
> public 클래스는 절대 가변 필드를 직접 노출해서는 안 된다. 불변 필드라면 노출해도 덜 위험하지만 완전히 안심할 수는 없다. 하지만 package-private 클래스나 private 중첩 클래스에서는 종종 필드를 노출하는 편이 나을 때도 있다.

### public 필드 대신 private + 접근자 사용하기
```java
class Point {
	public double x;
	public double y;
}
```

위의 클래스는 데이터 필드에 직접 접근할 수 있으니 캡슐화의 이점을 제공하지 못한다. API를 수정하지 않고는 내부 표현을 바꿀 수 없고, 불변식을 보장할 수 없으며, 외부에서 필드에 접근할 때 부수 작업을 수행할 수도 없다.

```java
public final class Time {
	public final int hour;
	public final int minute;
	//...
}
```

public 클래스의 필드가 불변이라면 단점이 조금은 줄어들지만 여전히 결코 좋은 생각이 아니다. API를 변경하지 않고는 표현 방식을 바꿀 수 없고, 여전히 필드를 읽을 때 부수 작업을 수행할 수 없다. 자바 플랫폼 라이브러리의 java.awt.package.Point, Dimension은 public 필드를 직접 노출하여 심각한 성능 문제를 가지고 있다.

```java
class Point {
	private double x;
	private double y;
	
	public Point(doublx x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }
}
```

따라서 필드를 모두 private으로 바꾸고 public 접근자(getter)를 추가한다.  

## 아이템 17. 변경 가능성을 최소화하라

불변 클래스란 그 인스턴스 내부 값을 수정할 수 없는 클래스다. 불변 인스턴스에 간직된 정보는 고정되어 객체가 파괴되는 순간까지 절대 달라지지 않는다. 자바 플랫폼 라이브러리에는 String, 기본 타입의 박싱 클래스들, BigInteger, BigDecimal이 불변 클래스에 속한다. 불변 클래스는 가변 클래스보다 설계/구현/사용이 쉬우며, 오류가 생길 여지도 적고 훨씬 안전하다.  

게터(getter)가 있다고 해서 무조건 세터(setter)를 만들지는 말자. 꼭 필요한 경우가 아니라면 모든 필드는 private final로 선언하여 불변 클래스로 만들어야 한다. 불변으로 만들 수 없는 클래스라도 변경할 수 있는 부분을 최소한으로 줄이자. 그리고 생성자는 불변식 설정이 모두 완료된, 초기화가 완벽히 끝난 상태의 객체를 생성해야 한다.

### 불변 클래스 만들기
클래스를 불변으로 만들려면 다음의 규칙을 따르면 된다.
- 객체의 상태를 변경하는 메서드(변경자, setter)를 제공하지 않는다.
- 클래스를 확장할 수 없도록 한다. 하위 클래스에서 부주의하게 객체의 상태를 변경하는 사태를 막는다.
- 모든 필드를 final로 선언한다. 
- 모든 필드를 private으로 선언한다.
- 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다. 클래스에 가변 객체를 참조하는 필드가 하나라도 있다면 클라이언트에서 그 객체의 참조를 얻을 수 없도록 해야 한다.

### 장점
- 불변 객체는 단순하다. 
- 불변 객체는 스레드 세이프하여 동기화할 필요가 없다.
- 불변 객체는 자유롭게 공유할 수 있고, 불변 객체끼리 내부 데이터를 공유할 수 있다.
- 불변 객체는 원자성을 제공한다.

### 상속 막기
```java
public class Complex {
	private final double re;
	private final double im;
	
	private Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}
	
	public static Complex valueOf(double re, double im) {
		return new Complex(re, im);
	}
}
```
불변 클래스를 만들려면 자신을 상속하지 못하게 해야 한다. 가장 쉬운 방법은 final 클래스로 선언하는 것이다. 또 다른 방법은 모든 생성자를 private 혹은 package-private으로 만들고 public 정적 팩터리를 제공하는 것이다.  

## 아이템 18. 상속보다는 컴포지션을 사용하라

## 아이템 19. 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라

## 아이템 20. 추상 클래스보다는 인터페이스를 우선하라

## 아이템 21. 인터페이스는 구현하는 쪽을 생각해 설계하라

## 아이템 22. 인터페이스는 타입을 정의하는 용도로만 사용하라

## 아이템 23. 태그 달린 클래스보다는 클래스 계층구조를 활용하라

## 아이템 24. 멤버 클래스는 되도록 static으로 만들라

## 아이템 25. 톱레벨 클래스는 한 파일에 하나만 담으라
