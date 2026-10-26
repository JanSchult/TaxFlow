import kotlinx.serialization.Serializable
sealed interface Screen {
    @Serializable
    data object Dashboard : Screen

    @Serializable
    data object AddTransaction : Screen

    @Serializable
    data object Overview : Screen

    @Serializable
    data object Deadlines : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Paywall : Screen

    @Serializable
    data object ReceiptScan : Screen

    @Serializable
    data object Export : Screen

    @Serializable
    data object Backup : Screen

    // @Serializable
    // data object Euer : Screen

    @Serializable
    data class PeriodDetail(
        val label: String,
        val from: String,
        val to: String
    ) : Screen
}