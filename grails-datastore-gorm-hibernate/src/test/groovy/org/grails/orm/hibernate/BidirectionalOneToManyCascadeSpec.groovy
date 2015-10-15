package org.grails.orm.hibernate

import grails.persistence.Entity
import spock.lang.Issue

/**
 * Tests that bidirectional one-to-many associations cascade correctly
 */
class BidirectionalOneToManyCascadeSpec extends GormSpec {

    @Issue('https://github.com/grails/grails-core/issues/9290')
    void "Test that child is saved correctly when associating only the owning side"() {
        when: "An owner is saved by the inverse child is not associated"
        Team padres = new Team(
                name: "Padres",
                city: "San Diego"
        )


        def contract = new Contract(
                expiration: new Date(),
                salary: 40_000_000
        )

        def player = new Player(
                firstName: "John",
                lastName: "Doe",
                position: "Pitcher",
                contract: contract
        )

        // workaround: this should not be needed!
//        contract.player = player

        padres.addToPlayers(player)
        padres.save(flush: true)
        session.clear()

        then: 'the team exists'
        Team.count() == 1

        and: 'the associations are created'
        player.team == padres
        contract.player == player
    }

    @Override
    List getDomainClasses() {
        [Team, Player, Contract]
    }

}

@Entity
class Team {

    String name
    String city

    static hasMany = [players: Player]

    static constraints = {
    }
}

@Entity
class Player {

    String firstName
    String lastName
    String position

    static belongsTo = [team: Team]
    static hasOne = [contract: Contract]
}

@Entity
class Contract {

    Date expiration
    BigDecimal salary

    static belongsTo = [player: Player]

    static constraints = {
    }
}
