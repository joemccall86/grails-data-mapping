package org.grails.orm.hibernate

import grails.persistence.Entity
import spock.lang.Issue

/**
 * Tests that bidirectional one-to-many associations cascade correctly
 */
class BidirectionalOneToManyCascadeSpec extends GormSpec {

    @Issue('https://github.com/grails/grails-core/issues/9290')
    void "Multiple levels are saved"() {
        when: "An owner is saved by the inverse child is not associated"
        def john = new TestPerson(
                head: new TestHead(
                        face: new TestFace()
                )
        )
        john.save(flush: true)
        session.clear()

        then: 'the person exists'
        TestPerson.count() == 1
    }

    @Override
    List getDomainClasses() {
        [
                TestPerson,
                TestHead,
                TestFace
        ]
    }

}

@Entity
class TestPerson {
    static hasOne = [head: TestHead]
}

@Entity
class TestHead {
    static belongsTo = [person: TestPerson]
    static hasOne = [face: TestFace]
}

@Entity
class TestFace {
    static belongsTo = [head: TestHead]
}
