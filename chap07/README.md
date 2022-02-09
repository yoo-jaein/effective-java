# Chapter 07 람다와 스트림

## 아이템 42. 익명 클래스보다는 람다를 사용하라 
> **핵심 정리**  
> 익명 클래스는 함수형 인터페이스가 아닌 타입의 인스턴스를 만들 때만 사용하라. 

### 함수 객체와 익명 클래스
예전에는 자바에서 함수 타입을 표현할 때 ```추상 메서드를 하나만 담은 인터페이스```를 사용했다. 이런 인터페이스의 인스턴스를 ```함수 객체(function object)```라고 불렀다. ```함수 객체```는 특정 함수나 동작을 나타내는 데 썼다. JDK 1.1부터 함수 객체를 만드는 주요 수단은 ```익명 클래스```였다.

```java
Collections.sort(words, new Comparator<String>() {
	public int compare(String s1, String s2) {
		return Integer.compare(s1.length(), s2.length());
	}
});
```

위 코드에서 Comparator 인터페이스는 정렬을 담당하는 추상 전략을 뜻한다. 문자열을 정렬하는 구체적인 전략은 ```익명 클래스```로 구현했다. 

### 함수형 인터페이스와 람다식
자바 8부터 ```추상 메서드 하나짜리 인터페이스```는 특별한 의미를 인정받게 된다. ```함수형 인터페이스```라 부르는 이 인터페이스들의 인스턴스를 ```람다식(lambda expression)```을 사용해 만들 수 있게 된 것이다. ```람다```는 ```함수```나 ```익명 클래스```와 개념은 비슷하지만 코드는 훨씬 간결하다. 

```java
Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
```

매개변수 s1, s2의 타입은 String이고 반환 값의 타입은 int지만 코드에서는 언급이 없다. 컴파일러가 문맥을 살펴 타입을 추론해준 것이다. 상황에 따라 컴파일러가 타입을 결정하지 못할 때는 프로그래머가 직접 명시해야 한다. 타입을 명시해야 코드가 더 명확할 때만 제외하고는, 기본적으로 ```람다```의 모든 매개변수 타입은 생략하자.  

```람다``` 자리에 비교자 생성 메서드 comparingInt를 사용하면 코드를 더 간결하게 만들 수 있다.

```java
Collections.sort(words, comparingInt(String::length));
```

자바 8 List 인터페이스에 추가된 sort 메서드를 이용하면 더욱 짧아진다.

```java
words.sort(comparingInt(String::length));
```

### apply 메서드 리팩터링하기
```java
public enum Operation {
	PLUS("+") {
		public double apply(double x, double y) {
			return x + y;
		}
	},
	MINUS("-") {
		public double apply(double x, double y) {
			return x - y;
		}
	};
	
	private final String symbol;
	
	Operation(String symbol) { 
		this.symbol = symbol; 
	}

	public abstract double apply(double x, double y);
}
```

6장의 아이템 34에서 Operation 열거 타입을 구현했었다. apply 메서드의 동작이 상수마다 달라야 해서 ```상수별 클래스 몸체```를 사용해 각 상수에서 apply 메서드를 재정의했었다. ```람다```를 이용하면 상수별로 다르게 동작하는 코드를 더욱 쉽게 구현할 수 있다. 각 상수의 동작을 ```람다```로 구현해 생성자에 넘기고, 생성자는 이 ```람다```를 인스턴스 필드로 저장해둔다. 그런 다음 apply 메서드에서 필드에 저장된 ```람다```를 호출하기만 하면 된다.

```java
import java.util.function.DoubleBinaryOperator;

public enum Operation {
	PLUS("+", (x, y) -> x + y),
	MINUS("-", (x, y) -> x - y);

	private final String symbol;
	private final DoubleBinaryOperator op;
	
	Operation(String symbol, DoubleBinaryOperator op) {
		this.symbol = symbol;
		this.op = op;
	}
	
	public double apply(double x, double y) {
		return op.applyAsDouble(x, y);
	}
}
```

열거 타입 상수의 동작을 ```DoubleBinaryOperator``` 인터페이스 변수에 할당했다. ```DoubleBinaryOperator```는 java.util.function 패키지가 제공하는 ```함수형 인터페이스``` 중 하나로, double 타입 인수 2개를 받아 double 타입 결과를 돌려준다. 

### 람다 사용시 주의사항
- 메서드나 클래스와 달리, ```람다```는 이름이 없고 문서화도 못 한다.
- ```람다```는 한 줄일 때 가장 좋고 길어야 세 줄 안에 끝내는 게 좋다.
- ```추상 클래스```의 인스턴스를 만들 때 ```람다```를 쓸 수 없으니 ```익명 클래스```를 써야 한다.
- ```람다```는 자기 자신을 참조할 수 없다. ```람다```에서의 this 키워드는 바깥 인스턴스를 가리킨다. (```익명 클래스```에서의 this는 익명 클랫의 인스턴스 자신을 가리킨다.)
- ```람다```는 직렬화 형태가 구현별로(예: 가상머신별로) 다를 수 있으므로 ```람다```를 직렬화하는 일을 삼가야 한다. (```익명 클래스```의 인스턴스도 마찬가지다.)

---

## 아이템 43. 람다보다는 메서드 참조를 사용하라
> **핵심 정리**  
> 메서드 참조가 짧고 명확하면 메서드 참조를 쓰고, 그렇지 않을 때만 람다를 사용하라.

자바에서는 함수 객체를 ```람다```보다도 더 간결하게 만드는 ```메서드 참조(method reference)```를 제공한다. 

```java
map.merge(key, 1, (count, incr) -> count + incr); // 람다
```

```java
map.merge(key, 1, Integer::sum); // 메서드 참조
```

보통은 ```람다```를 ```메서드 참조```로 대체하는 것이 좋지만, 항상 그런 것은 아니다. 때론 ```람다```가 ```메서드 참조```보다 간결할 때가 있다. 주로 메서드와 ```람다```가 같은 클래스에 있을 때 그렇다.

```java
service.execute(GoshThisClassNameIsHumongous::action);
```

```java
service.execute(() -> action());
```

---
## 아이템 44. 표준 함수형 인터페이스를 사용하라
> **핵심 정리**  
> 입력값과 반환값에 함수형 인터페이스 타입을 활용해보라. 보통은 java.util.function 패키지의 표준 함수형 인터페이스를 사용하는 것이 가장 좋은 선택이다.

자바가 람다를 지원하면서 API를 작성하는 모범 사례도 크게 바뀌었다. 예를 들어, 상위 클래스의 기본 메서드를 재정의해 원하는 동작을 구현하는 템플릿 메서드 패턴을 같은 효과의 함수 객체를 매개변수로 받는 정적 팩터리나 생성자를 제공하는 방식으로 대체할 수 있게 되었다. 이는 함수 객체(람다)를 매개변수로 받는 메서드가 늘어났다는 의미다.  

java.util.function 패키지에는 다양한 용도의 표준 함수형 인터페이스가 담겨있다. 필요한 용도에 맞는 게 있다면, 직접 구현하지 말고 [표준 함수형 인터페이스](https://github.com/yoo-jaein/modern-java-in-action/tree/master/src/chap03#%EC%9E%90%EB%B0%94-8%EC%9D%98-%ED%95%A8%EC%88%98%ED%98%95-%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4)를 활용하자.

---
## 아이템 45. 스트림은 주의해서 사용하라
> **핵심 정리**  
> 스트림이 적합한 곳이 있고 반복 방식이 적합한 곳이 있다. 그리고 수많은 작업이 이 둘을 조합했을 때 가장 멋지게 해결된다. 스트림과 반복 중 어느 쪽이 나은지 확신하기 어렵다면 둘 다 해보고 더 나은 쪽을 택하라.



---
## 아이템 46. 스트림에서는 부작용 없는 함수를 사용하라
> **핵심 정리**  
> 스트림 파이프라인 프로그래밍의 핵심은 부작용 없는 함수 객체에 있다. 스트림과 스트림 관련 객체에 건네지는 모든 함수 객체는 부작용이 없어야 한다. 



---
## 아이템 47. 반환 타입으로는 스트림보다 컬렉션이 낫다
> **핵심 정리**  
> 원소 시퀀스를 반환하는 메서드를 작성할 때는, 이를 스트림으로 처리하기를 원하는 사용자와 반복으로 처리하길 원하는 사용자가 모두 있음을 떠올리자.



---
## 아이템 48. 스트림 병렬화는 주의해서 적용하라
> **핵심 정리**  
> 확신 없이 스트림 파이프라인을 병렬화하지 말라. 스트림을 잘못 병렬화하면 프로그램을 오동작하게 하거나 성능을 급격히 떨어뜨린다. 병렬화된 코드가 계산이 정확하고 성능도 좋아졌음이 확실할 때만 그 코드를 운영 코드에 반영하라.

