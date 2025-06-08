package io.foreshore.cookware.util

class SnowflakeIdGenerator(
    private val epoch: Long,
    private val datacenterId: Long,
    private val machineId: Long
) {

    private var lastTimestamp = -1L
    private var sequence = 0L

    companion object {
        private const val UNUSED_BITS = 1 // Sign bit, Unused (always set to 0)
        private const val EPOCH_BITS = 41
        private const val DATACENTER_ID_BITS = 5
        private const val MACHINE_ID_BITS = 5
        private const val SEQUENCE_BITS = 12

        private const val MAX_DATACENTER_ID = (1L shl DATACENTER_ID_BITS) - 1
        private const val MAX_MACHINE_ID = (1L shl MACHINE_ID_BITS) - 1
        private const val MAX_SEQUENCE = (1L shl SEQUENCE_BITS) - 1

        private const val MACHINE_ID_SHIFT = SEQUENCE_BITS
        private const val DATACENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS
        private const val TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATACENTER_ID_BITS
    }

    init {
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw IllegalArgumentException("Datacenter ID must be between 0 and $MAX_DATACENTER_ID")
        }
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw IllegalArgumentException("Machine ID must be between 0 and $MAX_MACHINE_ID")
        }
    }

    @Synchronized
    fun nextId(): Long {
        var currentTimestamp = timestamp()

        if (currentTimestamp < lastTimestamp) {
            throw IllegalStateException("Clock moved backwards. Refusing to generate id for ${lastTimestamp - currentTimestamp} milliseconds")
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) and MAX_SEQUENCE
            if (sequence == 0L) {
                currentTimestamp = tilNextMillis(lastTimestamp)
            }
        } else {
            sequence = 0L
        }

        lastTimestamp = currentTimestamp

        return ((currentTimestamp - epoch) shl TIMESTAMP_SHIFT) or
                (datacenterId shl DATACENTER_ID_SHIFT) or
                (machineId shl MACHINE_ID_SHIFT) or
                sequence
    }

    private fun timestamp(): Long {
        return System.currentTimeMillis()
    }

    private fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = timestamp()
        while (timestamp <= lastTimestamp) {
            timestamp = timestamp()
        }
        return timestamp
    }
}
