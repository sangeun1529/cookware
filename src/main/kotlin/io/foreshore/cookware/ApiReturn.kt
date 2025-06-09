package io.foreshore.cookware

/**
 * API 응답의 오류 정보를 나타내는 데이터 클래스입니다.
 *
 * @property code 오류 코드 (일반적으로 문자열 형태).
 * @property title 오류 제목.
 * @property detail 오류에 대한 상세 설명 (선택 사항).
 */
data class Error(
    val code: String,
    val title: String,
    val detail: String? = null
) {
    /**
     * 예외(Throwable) 객체로부터 Error 인스턴스를 생성합니다.
     * 기본적으로 코드 값은 [CODE_EXCEPTION]을 사용하고, 제목은 예외 메시지를 사용합니다.
     *
     * @param throwable 오류를 나타내는 Throwable 객체.
     */
    constructor(throwable: Throwable) : this(
        code = CODE_EXCEPTION,
        title = throwable.message ?: "알 수 없는 오류", // throwable.message가 null일 경우 기본 메시지
        detail = throwable.toString()
    )

    /**
     * 정수형 오류 코드로부터 Error 인스턴스를 생성합니다.
     *
     * @param code 정수형 오류 코드.
     * @param title 오류 제목.
     * @param detail 오류에 대한 상세 설명 (선택 사항).
     */
    constructor(code: Int, title: String, detail: String? = null) : this(
        code = code.toString(),
        title = title,
        detail = detail
    )

    companion object {
        /**
         * 성공 상태를 나타내는 기본 오류 코드입니다. ("0")
         */
        const val CODE_OK: String = "0"

        /**
         * 일반적인 예외 발생 시 사용되는 기본 오류 코드입니다. ("500")
         */
        const val CODE_EXCEPTION: String = "500" // Java 원본에서는 Error 클래스 내에 있었음

        /**
         * 성공 상태를 나타내는 기본 오류 제목입니다. ("OK")
         */
        const val TITLE_OK: String = "OK" // Java 원본에서는 Error 클래스 내에 있었음

        /**
         * 미리 정의된 성공(OK) 상태의 Error 객체입니다.
         * 코드는 [CODE_OK], 제목은 [TITLE_OK]를 사용합니다.
         */
        @JvmField // Java와의 상호 운용성을 위해 추가 (선택 사항이지만, Java에서 Error.OK와 같이 접근 가능)
        val OK: Error = Error(CODE_OK, TITLE_OK)
    }
}

/**
 * 페이징된 API 응답의 페이지 정보를 나타내는 데이터 클래스입니다.
 *
 * @param U 페이지 내 컨텐츠의 데이터 타입.
 * @property totalElements 전체 아이템 개수.
 * @property content 현재 페이지의 아이템 리스트.
 * @property size 페이지 당 아이템 개수.
 * @property totalPages 전체 페이지 수.
 * @property last 현재 페이지가 마지막 페이지인지 여부.
 * @property first 현재 페이지가 첫 번째 페이지인지 여부.
 * @property number 현재 페이지 번호 (0부터 시작).
 * @property numberOfElements 현재 페이지의 아이템 개수.
 * @property empty 현재 페이지가 비어있는지 여부.
 */
data class Page<U>(
    val totalElements: Int,
    val content: List<U> = emptyList(),
    val size: Int = SIZE_DEFAULT,
    val number: Int = 0 // 기본 페이지 번호는 0으로 가정
) {
    val totalPages: Int = if (size > 0) (totalElements + size - 1) / size else 0
    val last: Boolean = number >= totalPages - 1
    val first: Boolean = number == 0
    val numberOfElements: Long = content.size.toLong()
    val empty: Boolean = content.isEmpty()

    companion object {
        /**
         * 페이지 당 기본 아이템 개수입니다. (10)
         */
        const val SIZE_DEFAULT: Int = 10

        /**
         * 전체 아이템 개수의 기본값입니다. (0)
         * 주로 빈 페이지를 생성할 때 사용될 수 있습니다.
         */
        const val TOTAL_ELEMENTS_DEFAULT: Int = 0
    }
}

/**
 * API 응답을 나타내는 제네릭 클래스입니다.
 *
 * @param T 응답 데이터의 타입.
 * @property error API 응답의 오류 정보. 성공 시에는 [Error.OK]와 유사한 상태를 가질 수 있으며,
 *                 실패 시에는 구체적인 오류 내용을 담습니다. Java 원본에서는 error가 NonNull이었으므로,
 *                 성공 시 기본 Error 객체를 갖도록 설계합니다. (예: Error.OK)
 * @property data API 응답의 실제 데이터 (선택 사항).
 */
data class ApiReturn<T>(
    val error: Error,
    val data: T?
) {
    companion object {
        /**
         * 데이터가 없는 성공적인 API 응답([Unit] 타입)을 반환합니다.
         * 오류 정보는 [Error.OK]를 사용합니다.
         *
         * @return 성공 상태의 `ApiReturn<Unit>`.
         */
        @JvmStatic // Java에서 ApiReturn.getOK() 형태로 호출 가능하도록
        fun getOK(): ApiReturn<Unit> = ApiReturn(Error.OK, Unit)

        /**
         * 주어진 데이터를 포함하는 성공적인 API 응답을 생성합니다.
         * 오류 정보는 [Error.OK]를 사용합니다.
         *
         * @param T 데이터의 타입.
         * @param data 응답에 포함될 데이터.
         * @return 데이터와 성공 상태를 포함하는 `ApiReturn<T>`.
         */
        @JvmStatic
        fun <T> of(data: T): ApiReturn<T> = ApiReturn(Error.OK, data)

        /**
         * 주어진 오류 정보와 데이터를 포함하는 API 응답을 생성합니다.
         *
         * @param T 데이터의 타입.
         * @param error 오류 정보를 담고 있는 [Error] 객체.
         * @param data 응답에 포함될 데이터 (선택 사항).
         * @return 오류 정보와 데이터를 포함하는 `ApiReturn<T>`.
         */
        @JvmStatic
        fun <T> of(error: Error, data: T?): ApiReturn<T> = ApiReturn(error, data)

        /**
         * 문자열 오류 코드, 제목, 상세 설명 및 데이터를 포함하는 API 응답을 생성합니다.
         *
         * @param T 데이터의 타입.
         * @param code 오류 코드.
         * @param title 오류 제목.
         * @param detail 오류 상세 설명 (선택 사항).
         * @param data 응답에 포함될 데이터 (선택 사항).
         * @return 생성된 오류 정보와 데이터를 포함하는 `ApiReturn<T>`.
         */
        @JvmStatic
        fun <T> of(code: String, title: String, detail: String?, data: T?): ApiReturn<T> {
            return ApiReturn(Error(code, title, detail), data)
        }

        /**
         * 정수형 오류 코드, 제목, 상세 설명 및 데이터를 포함하는 API 응답을 생성합니다.
         *
         * @param T 데이터의 타입.
         * @param code 정수형 오류 코드.
         * @param title 오류 제목.
         * @param detail 오류 상세 설명 (선택 사항).
         * @param data 응답에 포함될 데이터 (선택 사항).
         * @return 생성된 오류 정보와 데이터를 포함하는 `ApiReturn<T>`.
         */
        @JvmStatic
        fun <T> of(code: Int, title: String, detail: String?, data: T?): ApiReturn<T> {
            return ApiReturn(Error(code, title, detail), data)
        }

        /**
         * 주어진 오류 정보를 포함하고 데이터가 없는 API 응답([Unit] 타입)을 생성합니다.
         *
         * @param error 오류 정보를 담고 있는 [Error] 객체.
         * @return 오류 정보를 포함하는 `ApiReturn<Unit>`.
         */
        @JvmStatic
        fun errorOf(error: Error): ApiReturn<Unit> = ApiReturn(error, null)

        /**
         * 문자열 오류 코드, 제목, 상세 설명을 포함하고 데이터가 없는 API 응답([Unit] 타입)을 생성합니다.
         *
         * @param code 오류 코드.
         * @param title 오류 제목.
         * @param detail 오류 상세 설명 (선택 사항).
         * @return 생성된 오류 정보를 포함하는 `ApiReturn<Unit>`.
         */
        @JvmStatic
        fun errorOf(code: String, title: String, detail: String?): ApiReturn<Unit> {
            return ApiReturn(Error(code, title, detail), null)
        }

        /**
         * 정수형 오류 코드, 제목, 상세 설명을 포함하고 데이터가 없는 API 응답([Unit] 타입)을 생성합니다.
         *
         * @param code 정수형 오류 코드.
         * @param title 오류 제목.
         * @param detail 오류 상세 설명 (선택 사항).
         * @return 생성된 오류 정보를 포함하는 `ApiReturn<Unit>`.
         */
        @JvmStatic
        fun errorOf(code: Int, title: String, detail: String?): ApiReturn<Unit> {
            return ApiReturn(Error(code, title, detail), null)
        }

        /**
         * Throwable 객체로부터 오류 정보를 생성하고, 데이터가 없는 API 응답([Unit] 타입)을 생성합니다.
         *
         * @param throwable 오류를 나타내는 Throwable 객체.
         * @return 생성된 오류 정보를 포함하는 `ApiReturn<Unit>`.
         */
        @JvmStatic
        fun errorOf(throwable: Throwable): ApiReturn<Unit> {
            return ApiReturn(Error(throwable), null)
        }

        // Java 원본의 private static final ApiReturn<Unit> OK 필드는 getOK() 메소드로 대체되었습니다.
        // Kotlin에서는 companion object 프로퍼티로 유사하게 만들 수 있지만,
        // getOK() 함수가 명확성을 더해줄 수 있습니다.
        // 만약 필드 형태가 필요하다면 아래와 같이 추가할 수 있습니다:
        // @JvmField
        // val OK: ApiReturn<Unit> = ApiReturn(Error.OK, Unit)
        // 하지만 getOK()가 있으므로 중복으로 보일 수 있습니다.
    }
}

// 파일의 끝
