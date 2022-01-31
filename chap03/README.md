# Chapter 03 모든 객체의 공통 메서드

## 아이템 10. equals는 일반 규칙을 지켜 재정의하라
> **핵심 정리**  
> 꼭 필요한 경우가 아니면 equals를 재정의하지 말자. 대부분 Object의 equals가 비교를 정확히 수행해주며, 재정의해야 할 때는 그 클래스의 핵심 필드 모두를 빠짐없이, 다섯 가지 규약을 확실히 지켜야 한다.  

Object는 객체를 만들 수 있는 구체 클래스지만 기본적으로 상속해서 사용하도록 설계되었다. final이 아닌 메서드(equals, hashCode, toString, clone, finalize)는 모두 재정의(오버라이딩)를 염두하고 설계된 것이며 재정의 시 지켜야 하는 규약을 반드시 준수해야 한다. 

equals는 일반적으로 재정의하지 않는 것이 좋다.
Integer와 String처럼 값을 표현하는 값 클래스에서 두 값 객체를 equals로 비교할 때 값이 같은지 확인하기 위해 equals를 재정의할 필요가 있다. 값 클래스라 해도 값이 같은 인스턴스가 둘 이상 만들어지지 않음을 보장하는 인스턴스 통제 클래스라면 equals를 재정의하지 않아도 된다. 

equals를 재정의할 때는 반드시 일반 규약을 따라야 한다.
- x = x
- x = y이면 y = x
- x = y, y = z이면 x = z
- x = y 또는 x != y
- x != null

## 아이템 11. equals를 재정의하려거든 hashCode도 재정의하라
> **핵심 정리**  
> equals를 재정의할 때는 hashCode도 반드시 재정의해야 한다. 그렇지 않으면 프로그램이 정상 동작하지 않는다. AutoValue 프레임워크를 사용하면 equals와 hashCode를 자동으로 만들어준다.

## 아이템 12. toString을 항상 재정의하라
> **핵심 정리**  
> 모든 구체 클래스에서 Object의 toString을 재정의하자. 그러면 디버깅하기 쉬워진다.


## 아이템 13. clone 재정의는 주의해서 진행하라

## 아이템 14. Comparable을 구현할지 고려하라