# Chapter 03 모든 객체의 공통 메서드

## 아이템 10. equals는 일반 규칙을 지켜 재정의하라
> **핵심 정리**  
> 꼭 필요한 경우가 아니면 equals를 재정의하지 말자. 대부분 Object의 equals가 비교를 정확히 수행해주며, 재정의해야 할 때는 그 클래스의 핵심 필드 모두를 빠짐없이, 다섯 가지 규약을 확실히 지켜야 한다.  

Object는 객체를 만들 수 있는 구체 클래스지만 기본적으로 상속해서 사용하도록 설계되었다. final이 아닌 메서드(equals, hashCode, toString, clone, finalize)는 모두 재정의(오버라이딩)를 염두하고 설계된 것이며 재정의 시 지켜야 하는 규약을 반드시 준수해야 한다.  

equals는 일반적으로 재정의하지 않는 것이 좋다. Integer와 String처럼 값을 표현하는 값 클래스에서 두 값 객체를 equals로 비교할 때 값이 같은지 확인하기 위해 equals를 재정의할 필요가 있다. 값 클래스라 해도 값이 같은 인스턴스가 둘 이상 만들어지지 않음을 보장하는 인스턴스 통제 클래스라면 equals를 재정의하지 않아도 된다.  

### 일반 규약
equals를 재정의할 때는 반드시 일반 규약을 따라야 한다. 
- x = x
- x = y이면 y = x
- x = y, y = z이면 x = z
- x = y 또는 x != y
- x != null

### equals 재정의하기
양질의 equals 메서드를 구현하는 방법은 다음과 같다.  
0. Object 외의 타입을 매개변수로 받는 equals 메서드는 선언하지 말자. 입력 타입은 반드시 Object여야 한다.
1. 성능 최적화를 위해 == 연산자를 사용하여 입력이 자기 자신의 참조인지 확인한다. 
2. instanceof 연산자로 입력이 equals가 정의된 클래스인지 확인한다.
3. 입력을 올바른 타입으로 형변환한다.
4. 입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치하는지 하나씩 검사한다.
    - 특수한 부동소수 값을 다루는 float와 double 필드는 각각 Float.compare(float, float), Double.compare(double, double)로 비교하고 나머지 기본 타입 필드는 == 연산자로 비교한다. 참조 타입 필드는 각각의 equals 메서드로 비교한다. 필드를 비교하는 순서가 equals의 성능을 좌우하기도 하기 때문에 비교하는 비용이 싼 필드를 먼저 비교하자.

---

## 아이템 11. equals를 재정의하려거든 hashCode도 재정의하라
> **핵심 정리**  
> equals를 재정의할 때는 hashCode도 반드시 재정의해야 한다. 그렇지 않으면 프로그램이 정상 동작하지 않는다. AutoValue 프레임워크를 사용하면 equals와 hashCode를 자동으로 만들어준다.

equals를 재정의한 모든 클래스에서 hashCode도 재정의해야 한다. 그렇지 않으면 hashCode의 일반 규약을 어기게 되어 해당 클래스의 인스턴스를 HashMap이나 HashSet의 원소로 사용할 때 문제를 일으킬 것이다.  

### 일반 규약
hashCode는 다음의 일반 규약을 따라야 한다.
- equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 일관되게 항상 같은 값을 반환해야 한다. 단, 애플리케이션을 다시 실행한다면 이 값이 달라져도 상관없다.
- equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.
- equals(Object)가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다. 단, 다른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아진다.

### hashCode 재정의하기
논리적으로 같은 객체는 같은 해시코드를 반환해야 한다. Object의 기본 hashCode 메서드는 논리적으로 같지만 물리적으로 다른 객체들을 전혀 다르다고 판단하여 서로 다른 값을 반환한다. 따라서 equals를 재정의한 모든 클래스에서 hashCode도 함께 재정의해야 한다.  

또한, 이상적인 해시 함수는 서로 다른 인스턴스들을 32비트 정수 범위에 균일하게 분배해야 한다. 모든 객체에 똑같은 값을 내어주면 모든 객체가 해시테이블의 버킷 하나에 담겨 마치 연결 리스트처럼 동작하게 된다. 그러면 평균 수행 시간이 O(1)인 해시테이블이 O(N)으로 느려지게 된다.  

---

## 아이템 12. toString을 항상 재정의하라
> **핵심 정리**  
> 모든 구체 클래스에서 Object의 toString을 재정의하자. 그러면 디버깅하기 쉬워진다.

Object의 기본 toString 메서드는 PhoneNumber@adbbd처럼 단순히 [클래스 이름]@[16진수로 표시한 해시코드]를 반환한다. toString의 일반 규약에 따르면 '간결하면서 사람이 읽기 쉬운 형태의 유익한 정보'를 반환해야 한다. toString을 잘 구현하면 디버깅에 큰 도움이 된다. 일반적으로 그 객체가 가진 주요 정보 모두를 반환하는게 좋다.

---

## 아이템 13. clone 재정의는 주의해서 진행하라
> **핵심 정리**  
> 새로운 인터페이스를 만들 때는 절대 Cloneable을 확장해서는 안 되며, 새로운 클래스도 이를 구현해서는 안 된다. 기본 원칙은 '복제 기능은 생성자와 팩터리를 이용하는 게 최고'라는 것이다. 단, 배열만은 clone 메서드 방식이 가장 깔끔한, 이 규칙의 합당한 예외라 할 수 있다.

Cloneable은 복제해도 되는 클래스임을 명시하는 용도의 믹스인 인터페이스다. 이 인터페이스는 Object의 protected 메서드인 clone의 동작 방식을 결정한다. Cloneable을 구현한 클래스의 인스턴스에서 clone을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환하며, 그렇지 않은 클래스의 인스턴스에서 호출하면 CloneNotSupportedException을 던진다.  

### 일반 규약
clone은 다음의 일반 규약을 따라야 한다.
- x.clone() != x
- x.clone().getClass() == x.getClass()
- x.clone().equals(x) (일반적으로 참이지만 필수는 아니다.)

clone 메서드는 사실상 생성자와 같은 효과를 낸다. 즉, clone은 원본 객체에 아무런 해를 끼치지 않는 동시에 복제된 객체의 불변식을 보장해야 한다.  

### clone 재정의하기
Cloneable을 구현하는 클래스는 clone을 재정의 해야 한다. 접근 제한자는 public으로, 반환 타입은 클래스 자신으로 변경한다. 이 메서드는 가장 먼저 super.clone을 호출한 후 그 객체의 내부 '깊은 구조'에 숨어 있는 모든 가변 객체를 복사하고, 복사본이 가진 객체 참조 모두가 복사된 객체들을 가리키게 해야 한다. 또한, Cloneable을 구현한 스레드 세이프 클래스를 작성할 때 clone 메서드도 적절히 동기화해줘야 한다.  

### clone 재정의 대신 복사 생성자 이용하기 
위의 복잡한 과정을 겪는 대신 ```복사 생성자```와 ```복사 팩터리```를 이용하는 것이 좋다. ```복사 생성자```는 단순히 자신과 같은 클래스의 인스턴스를 인수로 받는 생성자를 말한다. ```복사 생성자```(변환 생성자)와 ```복사 팩터리```(변환 팩터리)는 해당 클래스가 구현한 인터페이스 타입의 인스턴스를 인수로 받을 수 있다. 이를 이용하면 클라이언트는 원본의 구현 타입에 얽매이지 않고 복제본의 타입을 직접 선택할 수 있다. 예컨대 HashSet 객체 hashSet을 TreeSet 타입으로 간단히 복제할 수 있다.

```java
HashSet<Student> hashSet = ...
TreeSet<Student> treeSet = new TreeSet<>(s);
```

---

## 아이템 14. Comparable을 구현할지 고려하라
> **핵심 정리**  
> 순서를 고려해야 하는 값 클래스를 작성한다면 꼭 Comparable 인터페이스를 구현하여, 그 인스턴스들을 쉽게 정렬하고, 검색하고, 비교 기능을 제공하는 컬렉션과 어우러지도록 해야 한다. compareTo 메서드에서 필드의 값을 비교할 때 <, > 연산자는 쓰지 말고, 박싱된 기본 타입 클래스가 제공하는 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 사용하자.

알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하자. hashCode 규약을 지키지 못하면 해시를 사용하는 클래스와 어울리지 못하듯, compareTo 규약을 지키지 못하면 비교를 활용하는 TreeSet, TreeMap, Collections, Arrays 등 비교를 활용하는 클래스와 어울리지 못한다. 

### 일반 규약
compareTo은 다음의 일반 규약을 따라야 한다.
- 이 객체와 주어진 객체의 순서를 비교한다. 이 객체가 주어진 객체보다 작으면 음의 정수를, 같으면 0을, 크면 양의 정수를 반환한다. 비교할 수 없는 타입의 객체가 주어지면 ClassCastException을 던진다.
- sgn(x.compareTo(y)) == -sgn(x.compareTo(y))
- x.compareTo(y) > 0 && y.compareTo(z) > 0이면, x.compareTo(x) > 0
- x.compareTo(y) == 0이면, sgn(x.compareTo(x)) == sgn(y.compareTo(z))
- (x.compareTo(y) == 0) == (x.equals(y)) (필수는 아니지만 꼭 지키는게 좋다. 그렇지 않으면 이 클래스의 순서는 equals와 일관되지 않다는 것을 명시해야 한다.)

### compareTo 구현하기
```java
public int compareTo(PhoneNumber pn) {
	int result = Short.compare(areaCode, pn.areaCode); // 가장 중요한 필드
	if (result == 0) {
		result = Short.compare(prefix, pn.prefix); // 두 번째로 중요한 필드
		if (result == 0)
			result = Short.compare(lineNum, pn.lineNum); // 세 번째로 중요한 필드
	}
	return result;
}
```
정수 기본 타입 필드를 비교할 때 박싱된 기본 타입 클래스들의 정적 메서드 compare를 이용하고, 가장 핵심적인 필드부터 비교하자.

```java
public int compareTo(PhoneNumber pn) {
	Comparator<PhoneNumber> comparator =
		comparingInt((PhoneNumber pn) -> pn.areaCode)
		.thenComparingInt(pn -> pn.prefix)
		.thenComparingInt(pn -> pn.lineNum);
	return comparator.compare(this, pn);
}
```
비교자 생성 메서드 comparingInt, thenComparingInt를 이용해 비교자를 생성할 수도 있다. long, double용 메서드, 객체 참조용 메서드 comparing과 thenComparing도 정의되어 있다.
