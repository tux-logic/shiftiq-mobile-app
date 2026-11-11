package com.tuxlogic.shiftiq.mobile.feature.iot.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.UUID

class IotValueObjectsTest {

    @Test
    fun `DeviceId should parse valid UUID and string representation`() {
        val uuid = UUID.randomUUID()
        val deviceId = DeviceId(uuid)
        val deviceIdFromStr = DeviceId(uuid.toString())

        assertEquals(uuid, deviceId.value)
        assertEquals(deviceId, deviceIdFromStr)
        assertEquals(uuid.toString(), deviceId.toString())
    }

    @Test
    fun `RegistrationId and SnapshotId and DtcAlertId should parse valid UUID`() {
        val uuid = UUID.randomUUID()
        val regId = RegistrationId(uuid)
        val snapId = SnapshotId(uuid)
        val dtcId = DtcAlertId(uuid)

        assertEquals(uuid, regId.value)
        assertEquals(uuid, snapId.value)
        assertEquals(uuid, dtcId.value)
    }

    @Test
    fun `MacAddress should accept valid MAC and reject invalid`() {
        val validMac = MacAddress("00:1B:44:11:3A:B7")
        assertEquals("00:1B:44:11:3A:B7", validMac.toString())

        val validHyphen = MacAddress("00-1B-44-11-3A-B7")
        assertEquals("00-1B-44-11-3A-B7", validHyphen.toString())

        assertThrows(IllegalArgumentException::class.java) {
            MacAddress("invalid-mac")
        }
        assertThrows(IllegalArgumentException::class.java) {
            MacAddress("00:1B:44:11:3A")
        }
    }

    @Test
    fun `Vin should accept valid 17-character VIN and reject invalid`() {
        val validVin = Vin("9BRBL3HE2P0123456")
        assertEquals("9BRBL3HE2P0123456", validVin.toString())

        assertThrows(IllegalArgumentException::class.java) {
            Vin("TOO_SHORT")
        }
        assertThrows(IllegalArgumentException::class.java) {
            Vin("9BRBL3HE2P012345I") // Contains 'I', illegal in standard VIN
        }
    }

    @Test
    fun `PlateNumber should accept valid plate and reject invalid`() {
        val validPlate = PlateNumber("ABC-123")
        assertEquals("ABC-123", validPlate.toString())

        assertThrows(IllegalArgumentException::class.java) {
            PlateNumber("")
        }
        assertThrows(IllegalArgumentException::class.java) {
            PlateNumber("A") // Too short
        }
        assertThrows(IllegalArgumentException::class.java) {
            PlateNumber("TOO_LONG_PLATE_NUMBER")
        }
    }
}
