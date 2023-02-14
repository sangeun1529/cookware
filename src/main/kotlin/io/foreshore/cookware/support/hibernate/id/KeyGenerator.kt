@file:Suppress("unused")

package io.foreshore.cookware.support.hibernate.id

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.Configurable
import org.hibernate.id.IdentifierGenerator
import org.hibernate.id.enhanced.SequenceStyleGenerator
import org.hibernate.internal.util.config.ConfigurationHelper
import org.hibernate.service.ServiceRegistry
import org.hibernate.type.LongType
import org.hibernate.type.Type
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Usage:
 *
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
@GenericGenerator(name = "book_seq", strategy = "io.foreshore.cookware.support.hibernate.id.StringPrefixedSequenceKeyGenerator",
parameters = [
Parameter(name = StringPrefixedSequenceKeyGenerator.VALUE_PREFIX_PARAMETER, value = "PREFIX_"),
Parameter(name = StringPrefixedSequenceKeyGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
])
 * @author kkomac
 * @date 2023/02/14
 */
class StringPrefixedSequenceKeyGenerator : SequenceStyleGenerator() {
    private var valuePrefix: String? = null
    private var numberFormat: String? = null

    override fun generate(sharedSessionContractImplementor: SharedSessionContractImplementor, obj: Any): Serializable =
        valuePrefix + String.format(numberFormat!!, super.generate(sharedSessionContractImplementor, obj))

    override fun configure(type: Type?, params: Properties?, serviceRegistry: ServiceRegistry?) {
        super.configure(LongType.INSTANCE, params, serviceRegistry)
        valuePrefix = ConfigurationHelper.getString(VALUE_PREFIX_PARAMETER, params, VALUE_PREFIX_DEFAULT)
        numberFormat = ConfigurationHelper.getString(NUMBER_FORMAT_PARAMETER, params, NUMBER_FORMAT_DEFAULT)
    }

    companion object {
        const val VALUE_PREFIX_PARAMETER = "valuePrefix"
        const val VALUE_PREFIX_DEFAULT = ""
        const val NUMBER_FORMAT_PARAMETER = "numberFormat"
        const val NUMBER_FORMAT_DEFAULT = "%d"
    }
}

class DatePrefixedSequenceKeyGenerator : SequenceStyleGenerator() {
    private var dateFormat: DateTimeFormatter? = null
    private var separator: String? = null

    override fun generate(sharedSessionContractImplementor: SharedSessionContractImplementor, obj: Any): Serializable =
        dateFormat?.format(LocalDateTime.now()) ?: ("" + separator + super.generate(
            sharedSessionContractImplementor, obj
        ))

    override fun configure(type: Type?, params: Properties?, serviceRegistry: ServiceRegistry?) {
        super.configure(LongType.INSTANCE, params, serviceRegistry)
        separator = ConfigurationHelper.getString(SEPARATOR_PARAMETER, params, SEPARATOR_DEFAULT)
        dateFormat = DateTimeFormatter.ofPattern(
            ConfigurationHelper.getString(DATE_FORMAT_PARAMETER, params, DATE_FORMAT_DEFAULT)
        )
    }

    companion object {
        const val SEPARATOR_PARAMETER = "separator"
        const val SEPARATOR_DEFAULT = "-"
        const val DATE_FORMAT_PARAMETER = "numberFormat"
        const val DATE_FORMAT_DEFAULT = "yyyyMMdd"
    }
}


/** Hibernate 의 UUID generator 는 완전 랜덤이 아닌 GUID 스펙 그대로이다.
@GenericGenerator(name = "guidGenerator", strategy = "org.hibernate.id.UUIDHexGenerator", parameters = [Parameter(name = "separator", value = "-")])
@GeneratedValue(generator = "guidGenerator")
따라서 완전한 랜덤 GUID 를 생성한다.

 * Usage -
@GenericGenerator(name = "guidGenerator", strategy = "io.foreshore.cookware.support.hibernate.id.StringGuidKeyGenerator",
parameters = [Parameter(name = StringGuidKeyGenerator.LOWERCASE_PARAMETER, value = true)])
@GeneratedValue(generator = "guidGenerator")

param : lowerCase default is true
 */
class StringGuidKeyGenerator : IdentifierGenerator, Configurable {
    private var lowerCase = true

    override fun generate(sharedSessionContractImplementor: SharedSessionContractImplementor, obj: Any): Serializable =
        UUID.randomUUID().apply { if (lowerCase) this.toString() else this.toString().uppercase() }

    override fun configure(type: Type?, params: Properties?, serviceRegistry: ServiceRegistry?) {
        lowerCase = ConfigurationHelper.getBoolean(LOWERCASE_PARAMETER, params, LOWERCASE_DEFAULT)
    }

    override fun supportsJdbcBatchInserts() = false

    companion object {
        const val LOWERCASE_PARAMETER = "lowerCase"
        const val LOWERCASE_DEFAULT = true
    }
}
