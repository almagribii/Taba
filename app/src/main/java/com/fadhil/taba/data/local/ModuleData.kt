package com.fadhil.taba.data.local

import com.fadhil.taba.R
import com.fadhil.taba.data.model.Module
import com.fadhil.taba.data.model.ModuleVocabulary
import com.fadhil.taba.data.model.ModuleQuestion

object ModuleData {
    val modules = listOf(
        Module(
            id = 1,
            title = "Kuda",
            titleEn = "Horse",
            arabicTitle = "الْحِصَانُ",
            content = "لِلرَّجُلِ حِصَانٌ. الْحِصَانُ حَيَوَانٌ لَطِيفُ الشَّكْلِ. هُوَ يَجْرِي بِسُرْعَةٍ فائقة، نَسْتَعْمِلُهُ كَثِيرًا فِي الرُّكُوبِ، fَنَضَعُ عَلَى ظَهْرِهِ سَرْجًا مِنَ الْجِلْدِ وَفِي fا مه لجاما نَشَدُّ بِهِ. فِي رِجْلِهِ حَافِرٌ كَبِيرٌ وَفِي طَرَفِهَا نَعْلٌ مِنَ الْحَدِيدِ لِمَنْعِ الْأَلِم مِنَ الحِجَارَةِ وَغَيْرِهَا. الْحِصَانُ يَحْمِلُ الْأَثْقَالَ وَيَجُرُّ الْعَجَلَاتِ الْفَارِسُ يَرْكَبُ الْحِصَانَ لِلسبَاقِ. بيْتُ الْحِصَانُ فِي إِصْطَبْلِهِ وَيَأْكُلُ الْعَلَفَ مِنَ الْحَشَائِشِ.",
            vocabularies = listOf(
                ModuleVocabulary("حِصَانٌ", "Kuda", "Horse", R.drawable.kuda),
                ModuleVocabulary("رَجُلٌ", "Laki-laki", "Man"),
                ModuleVocabulary("حَيَوَانٌ", "Hewan", "Animal"),
                ModuleVocabulary("يَجْرِي", "Berlari", "Running"),
                ModuleVocabulary("فَائِقَةٌ", "Sangat tinggi", "Very high"),
                ModuleVocabulary("الرُّكُوبُ", "Menunggang", "Riding"),
                ModuleVocabulary("سَرْجٌ", "Pelana", "Saddle"),
                ModuleVocabulary("لِجَامٌ", "Kekang", "Bridle"),
                ModuleVocabulary("فَارِسٌ", "Penunggang kuda", "Rider"),
                ModuleVocabulary("حَافِرٌ", "Kuku kaki kuda", "Hoof"),
                ModuleVocabulary("إِصْطَبْلٌ", "Kandang kuda", "Stable"),
                ModuleVocabulary("الْعَلَفُ", "Pakan ternak", "Fodder")
            ),
            questions = listOf(
                ModuleQuestion("لِمَنْ الْحِصَانُ ؟", "Milik siapa kuda itu?", "Whose horse is it?"),
                ModuleQuestion("مَا هُوَ الْحِصَانُ ؟", "Apa itu kuda?", "What is a horse?"),
                ModuleQuestion("كَيْفَ يَجْرِي الْحِصَانُ؟", "Bagaimana kuda berlari?", "How does the horse run?"),
                ModuleQuestion("لِأَيِّ شَيْءٍ نَسْتَعْمِلُ الْحِصَانَ؟", "Untuk apa kita menggunakan kuda?", "What do we use the horse for?"),
                ModuleQuestion("مَاذَا نَضَعُ عَلَى ظَهْرِ الْحِصَانِ وَفِيهِ؟", "Apa yang kita letakkan di punggung kuda dan di dalamnya (mulut)?", "What do we put on the horse's back and in it (mouth)?"),
                ModuleQuestion("أَيْنَ الْحَافِرُ؟ مَاذَا فِي طَرَفِ الْحَافِرِ؟ وَمَا فَائِدَتُهَا؟", "Di mana kuku kuda? Apa yang ada di ujung kuku? Dan apa gunanya?", "Where is the hoof? What is at the tip of the hoof? And what is its use?"),
                ModuleQuestion("مَاذَا يَحْمِلُ الْحِصَانُ؟ وَمَاذَا يَجُرُّ؟", "Apa yang dibawa kuda? Dan apa yang ditariknya?", "What does the horse carry? And what does it pull?"),
                ModuleQuestion("مَنْ يَرْكَبُ الْحِصَانَ لِلرِّبَاقِ؟", "Siapa yang menunggang kuda untuk balapan?", "Who rides the horse for the race?"),
                ModuleQuestion("أَيْنَ يَبَيْتُ الْحِصَانُ؟ مَا بَيْتُ الْحِصَانِ؟", "Di mana kuda bermalam? Apa sebutan untuk rumah kuda?", "Where does the horse stay? What is the horse's house?"),
                ModuleQuestion("مَاذَا يَأْكُلُ الْحَصَانُ؟", "Apa yang dimakan kuda?", "What does the horse eat?")
            ),
            imageResId = R.drawable.kuda
        ),
        Module(
            id = 2,
            title = "Kereta Api",
            titleEn = "Train",
            arabicTitle = "القِطَارُ",
            content = "فِي الْمَدِينَةِ قِطَارٌ. لَهُ عَجَلَاتٌ كَثِيرَةٌ مِنَ الْحَدِيدِ. الْقِطَارُ يَسِيرُ عَلَى القُضْبانِ، قَائِدُ الْقِطَارِ يَسُوقُ الْقِطَارَ مِنْ مَحَطَّةٍ إِلَى مَحَطَّةٍ أُخْرَى. يَقِفُ الْقِطَارُ في الْمَحَطَّةِ. لِلْقِطَارِ قَاطِرَةٌ تَجُرُّ عَرَبَاتٍ. فِي الْعَرَبَةِ رُكَّابٌ كَثِيرُونَ. الْمُسَافِرُونَ يشترونَ التَّذْكِرَةَ فِي شُبَّاكِ التَّذَاكِرِ قَبْلَ السَّفَرِ.",
            vocabularies = listOf(
                ModuleVocabulary("قِطَارٌ", "Kereta api", "Train"),
                ModuleVocabulary("مَدِينَةٌ", "Kota", "City"),
                ModuleVocabulary("عَجَلَاتٌ", "Roda", "Wheels"),
                ModuleVocabulary("قُضْبَانٌ", "Rel kereta", "Rails"),
                ModuleVocabulary("قَائِدٌ", "Masinis", "Driver / Engineer"),
                ModuleVocabulary("مَحَطَّةٌ", "Stasiun", "Station"),
                ModuleVocabulary("قَاطِرَةٌ", "Lokomotif", "Locomotive"),
                ModuleVocabulary("رُكَّابٌ", "Penumpang", "Passengers"),
                ModuleVocabulary("مُسَافِرُونَ", "Para pelancong", "Travelers"),
                ModuleVocabulary("تَذْكِرَةٌ", "Tiket", "Ticket")
            ),
            questions = listOf(
                ModuleQuestion("مَاذَا فِي الْمَدِينَة؟ كَمْ عَجَلَةٌ لَهُ؟", "Ada apa di kota? Berapa roda yang ia miliki?", "What is in the city? How many wheels does it have?"),
                ModuleQuestion("عَلَى أَيِّ شَيْءٍ يَسِيرُ الْقِطَارُ؟", "Di atas apa kereta itu berjalan?", "On what does the train run?"),
                ModuleQuestion("مَنْ يَسُوقُ الْقِطَارُ؟", "Siapa yang mengemudikan kereta?", "Who drives the train?"),
                ModuleQuestion("أَيْنَ يَقِفُ الْقِطَارُ؟", "Di mana kereta itu berhenti?", "Where does the train stop?"),
                ModuleQuestion("كَمْ رَاكِبًا فِي الْعَرَبَةِ؟", "Berapa banyak penumpang di gerbong?", "How many passengers are in the carriage?"),
                ModuleQuestion("مَاذَا لِلْقِطَارِ؟ وَمَاذَا تَجُرُّ؟", "Apa yang dimiliki kereta? Dan apa yang ditariknya?", "What does the train have? And what does it pull?"),
                ModuleQuestion("أَيْنَ يَشْتَرِي الْمُسَافِرُونَ التَّذْكِرَةَ؟", "Di mana para penumpang membeli tiket?", "Where do the travelers buy the ticket?")
            ),
            imageResId = R.drawable.kereta
        ),
        Module(
            id = 3,
            title = "Salat",
            titleEn = "Prayer",
            arabicTitle = "الصَّلاة",
            content = "الصَّلَاةُ وَاجبَةٌ عَلَى الْمُسْلِمِينَ. هُنَاكَ صَلَوَاتٌ مَفْرُوضَةٌ وَهُنَاكَ صَلَوَاتٌ نافل الصَّلَوَاتُ الْمَفْرُوضَةُ خَمْسٌ هِيَ: صَلَاةُ الصُّبْحِ, وَصَلَاةُ الظُّهْرِ, وَصَلَاةُ عَصْرِ, وَصَلَاةُ الْمَغْرِبِ, وَصَلَاةُ الْعِشَاءِ. صَلَاةُ الصُّبْحِ فِي الْفَجْرِ، وَصَلَاةُ الظُّهْرِ العصرِ فِي Nَهَارِ، وَصَلَاةُ الْمَغْرِبِ وَالْعِشَاءِ فِي اللَّيْلِ.\n\nوَمِنَ الصَّلَوَاتِ النَّوَافِلِ هِيَ: صَلَاةُ الرَّوَاتِبِ وَصَلَاةُ الضُّحَى, وَصَلَاةُ بَعْدِ وَصَلَاةُ الْوِتْرِ/ وَصَلَاةُ التَّرَاوِيحَ، وَصَلَاةُ عِيْدِ الْفِطْرِ, وَصَلَاةُ عِيْدِ الْأَضْحَى. وَصَلَاةُ الضُّحَى فِي الصَّبَاحِ، وَصَلَاةُ التَّهَجُدِ وَالْوِتْرِ فِي اللَّيْلِ، وَصَلَاةُ التَّرَاوِيح في شَهْرِ رَمَضَانَ، وَصَلَاةُ عِيْدِ الْفِطْرِ فِي أَوَّلِ شَهْرِ شَوَّالٍ، وَصَلَاةُ عِيْدِ الْأَضْحَى ي الْعَاشِرِ مِنْ شَهْرِ ذِي الْحِجَّةِ.",
            vocabularies = listOf(
                ModuleVocabulary("الصَّلَاةُ", "Salat", "Prayer"),
                ModuleVocabulary("الْمُسْلِمُونَ", "Orang-orang Islam", "Muslims"),
                ModuleVocabulary("وَاجِبَةٌ", "Wajib", "Obligatory"),
                ModuleVocabulary("مَفْرُوضَةٌ", "Fardu", "Mandatory"),
                ModuleVocabulary("نَافِلَةٌ", "Sunah", "Optional / Sunnah"),
                ModuleVocabulary("الْفَجْرُ", "Fajar", "Dawn"),
                ModuleVocabulary("الظُّهْرُ", "Zuhur", "Noon"),
                ModuleVocabulary("الْعَصْرُ", "Asar", "Afternoon"),
                ModuleVocabulary("الْمَغْرِبُ", "Magrib", "Sunset"),
                ModuleVocabulary("الْعِشَاءُ", "Isya", "Night")
            ),
            questions = listOf(
                ModuleQuestion("مَا حُكْمُ الصَّلَاةِ عَلَى الْمُسْلِمِينَ ؟", "Apa hukum shalat bagi umat Islam?", "What is the ruling of prayer for Muslims?"),
                ModuleQuestion("أُذْكُرْ أَنْوَاعَ الصَّلَوَاتِ!", "Sebutkan jenis-jenis shalat!", "Mention the types of prayers!"),
                ModuleQuestion("مَا هِيَ الصَّلَوَاتُ الْمَفْرُوضَةُ! وَمَا هِيَ الصَّلَوَاتُ النَّوَافِلُ؟", "Apa saja shalat fardu? Dan apa saja shalat sunnah?", "What are the mandatory prayers? And what are the optional prayers?"),
                ModuleQuestion("مَتَى صَلَاةُ الصُّبْحِ ؟ وَمَتَى صَلَاةُ الْعَصْرِ؟ وَمَتَى صَلَاةُ الْعِشَاءِ؟", "Kapan shalat Subuh? Kapan shalat Ashar? Dan kapan shalat Isya?", "When is the Subuh prayer? When is the Ashar prayer? And when is the Isya prayer?"),
                ModuleQuestion("مَتَى صَلَاةُ الضُّحَى؟ وَمَتَى صَلَاةُ التَّهَجُدِ؟", "Kapan shalat Dhuha? Dan kapan shalat Tahajud?", "When is the Dhuha prayer? And when is the Tahajud prayer?"),
                ModuleQuestion("مَتَى صَلَاةُ التَّرَاوِيحُ ؟ وَمَتَى صَلَاةُ عِيْدِ الْفِطْرِ؟", "Kapan shalat Tarawih? Dan kapan shalat Idul Fitri?", "When is the Tarawih prayer? And when is the Eid al-Fitr prayer?")
            ),
            imageResId = R.drawable.sholat
        ),
        Module(
            id = 4,
            title = "Kerbau",
            titleEn = "Buffalo",
            arabicTitle = "الجاموس",
            content = "لفلاح جاموسٌ. الجاموس حَيَوَانُ كَبِيرُ الْجِسْمِ. لَهُ قَرْنَانِ طَوِيلان و هُوَ يَأْكُلُ الأَعْشَابَ، وَيَشْرَبُ مِيَاهَ التُّرْعَةِ، وَيَغْتَسِلُ فِي مَاءِ النَّهْرِ. الْجَامُوس يحرث الْأَرْضَ، وَيَجْرُ عَرَبَاتِ النَّقْلِ. كَرِيمٌ يَرْكَبُ الْجَامُوْسَ عَلَى ظَهْرِهِ وَيَرعاه  الي الحقل ، ثم يرجع به ثم في المساء.",
            vocabularies = listOf(
                ModuleVocabulary("جَامُوسٌ", "Kerbau", "Buffalo"),
                ModuleVocabulary("فَلَّاحٌ", "Petani", "Farmer"),
                ModuleVocabulary("حَيَوَانٌ", "Hewan", "Animal"),
                ModuleVocabulary("قَرْنَانِ", "Dua tanduk", "Two horns"),
                ModuleVocabulary("الأَعْشَابُ", "Rumput-rumput", "Grasses"),
                ModuleVocabulary("النَّهْرُ", "Sungai", "River"),
                ModuleVocabulary("الْأَرْضُ", "Tanah", "Land / Earth"),
                ModuleVocabulary("يَحْرُثُ", "Membajak", "Plowing"),
                ModuleVocabulary("يَرْكَبُ", "Menunggang", "Riding"),
                ModuleVocabulary("الْحَقْلُ", "Sawah", "Field")
            ),
            questions = listOf(
                ModuleQuestion("مَاذَا لِلْفَلَّاحِ ؟", "Apa yang dimiliki petani?", "What does the farmer have?"),
                ModuleQuestion("مَا هُوَ الْجَامُوسُ ؟", "Apa itu kerbau?", "What is a buffalo?"),
                ModuleQuestion("كَمْ قَرْنَا لِلْجَامُوسِ ؟", "Berapa tanduk yang dimiliki kerbau?", "How many horns does the buffalo have?"),
                ModuleQuestion("مَاذَا يَأْكُلُ الْجَامُوسُ؟ وَمَاذَا يَشْرَبُ؟", "Apa yang dimakan kerbau? Dan apa yang diminumnya?", "What does the buffalo eat? And what does it drink?"),
                ModuleQuestion("أَيْنَ يَغْتَسِلُ الْجَامُوسُ ؟ وَمَاذَا يَعْمَلُ؟", "Di mana kerbau mandi? Dan apa yang dilakukannya?", "Where does the buffalo wash itself? And what does it do?"),
                ModuleQuestion("أَيْنَ يَرْعَى كَرِيمٌ الْجَامُوس ؟", "Di mana Karim menggembala kerbau?", "Where does Karim graze the buffalo?"),
                ModuleQuestion("مَتَى يَرْجِعُ كَرِيمٌ بِالْجَامُوسِ ؟", "Kapan Karim pulang membawa kerbau?", "When does Karim return with the buffalo?")
            ),
            imageResId = R.drawable.kerbau
        ),
        Module(
            id = 5,
            title = "Ayam Betina",
            titleEn = "Hen",
            arabicTitle = "الدَّجَاجَةُ",
            content = "وَرَاءَ الْبَيْتِ دَجَاجَةٌ. الدَّجَاجَةُ تَبِيضُ الْبَيْضَ، وَهِيَ تُرْخِمُ بَيْضَاتِهَا  ثلاثة أَسَابِعَ، وَتَفْقِسُهَا، ثُمَّ تَخْرُجُ مِنْهَا أَفْرَاخٌ، فَتُرَبِّي أَفْرَاخهَا وَتَحْرُسُهَا فِي كُلِّ وقت.",
            vocabularies = listOf(
                ModuleVocabulary("دَجَاجَةٌ", "Ayam betina", "Hen"),
                ModuleVocabulary("بَيْتٌ", "Rumah", "House"),
                ModuleVocabulary("تَبِيضُ", "Bertelur", "Laying eggs"),
                ModuleVocabulary("بَيْضٌ", "Telur", "Egg"),
                ModuleVocabulary("تُرَبِّي", "Memelihara", "Raising"),
                ModuleVocabulary("أَفْرَاخٌ", "Anak-anak ayam", "Chicks"),
                ModuleVocabulary("تَفْقِسُ", "Menetas", "Hatching"),
                ModuleVocabulary("أَسَابِيعُ", "Minggu-minggu", "Weeks"),
                ModuleVocabulary("تَحْرُسُ", "Menjaga", "Guarding"),
                ModuleVocabulary("وَقْتٌ", "Waktu", "Time")
            ),
            questions = listOf(
                ModuleQuestion("مَاذَا وَرَاءَ الْبَيْتِ؟", "Apa yang ada di belakang rumah?", "What is behind the house?"),
                ModuleQuestion("مَاذَا تَبِيضُ الدَّجَاجَةُ؟", "Apa yang dihasilkan (telur) oleh ayam betina?", "What does the hen lay?"),
                ModuleQuestion("كَمْ أَسْبُوعًا تَرْخِمُ الدَّجَاجَةُ بَيْضَاتِهَا؟", "Berapa minggu ayam betina mengerami telurnya?", "How many weeks does the hen sit on its eggs?"),
                ModuleQuestion("مَاذَا تَخْرُجُ مِنَ الْبَيْضَةِ؟", "Apa yang keluar dari telur?", "What comes out of the egg?"),
                ModuleQuestion("مَتَى تَحْرُسُ الدَّجَاجَةُ أَفْرَانَهَا؟", "Kapan ayam betina menjaga anak-anaknya?", "When does the hen guard its chicks?")
            ),
            imageResId = R.drawable.ayam
        ),
        Module(
            id = 6,
            title = "Gajah",
            titleEn = "Elephant",
            arabicTitle = "الْفِيلُ",
            content = "فِي حَدِيقَةِ الْحَيَوَانَاتِ فِيلٌ. الْفِيلُ حَيَوَانٌ كَبِيرُ الْحَجْمِ. لَهُ عَيْنَانِ صَغِيرَتَانِ وَاذُنَانِ وَاسِعَتَانِ وَعَاجَانِ طَوِيلَانِ. ذَيْلُهُ قَصِيرٌ وَرِجْلُهُ كَبِيرٌ كَالْبِرْمِيلِ. وَهُوَ يأْكُلُ الْأَعْشَابَ وَالْفَوَاكِهَ وَالْحَضْرَوَاتِ، يَأْخُذُهَا بِالْخُرْطُوْمِ ثُمَّ يُدْخِلُهَا إِلَى فمه. يَنقُلُ الْفِيْلُ شَيْئًا ثَقِيلًا عَلَى ظَهْرِهِ، نَرَى الْفِيْلَ الْيَوْمَ يَلْعَبُ بِالْكُرَةِ.",
            vocabularies = listOf(
                ModuleVocabulary("فِيلٌ", "Gajah", "Elephant"),
                ModuleVocabulary("حَدِيقَةُ الْحَيَوَانَاتِ", "Kebun binatang", "Zoo"),
                ModuleVocabulary("حَيَوَانٌ", "Hewan", "Animal"),
                ModuleVocabulary("كَبِيرُ الْحَجْمِ", "Berukuran besar", "Large size"),
                ModuleVocabulary("عَيْنٌ", "Mata", "Eye"),
                ModuleVocabulary("أُذُنٌ", "Telinga", "Ear"),
                ModuleVocabulary("عَاجٌ", "Gading", "Ivory / Tusk"),
                ModuleVocabulary("خُرْطُومٌ", "Belalai", "Trunk"),
                ModuleVocabulary("أَعْشَابٌ", "Rumput", "Grass"),
                ModuleVocabulary("فَوَاكِهُ", "Buah-buahan", "Fruits")
            ),
            questions = listOf(
                ModuleQuestion("أَيْنَ الْفِيْلُ؟ مَا هُوَ الْفِيلُ ؟", "Di mana gajah itu? Apa itu gajah?", "Where is the elephant? What is an elephant?"),
                ModuleQuestion("مَاذَا لِلْفِيلِ ؟", "Apa yang dimiliki gajah?", "What does the elephant have?"),
                ModuleQuestion("هَلْ ذَيْلُ الْفِيلِ طَوِيلُ؟ وَهَلْ رِجْلُهُ كَبِيرُ؟", "Apakah ekor gajah panjang? Dan apakah kakinya besar?", "Is the elephant's tail long? And is its leg big?"),
                ModuleQuestion("مَاذَا يَأْكُلُ الْفِيْلُ؟ وَكَيْفَ يَأْكُلُ؟", "Apa yang dimakan gajah? Dan bagaimana ia makan?", "What does the elephant eat? And how does it eat?"),
                ModuleQuestion("مَاذَا يَنقُلُ الْفِيْلُ عَلَى ظَهْرِهِ؟ مَاذَا نَرَى الْفِيْلَ الْيَوْمَ؟", "Apa yang dibawa gajah di punggungnya? Apa yang kita lihat gajah lakukan hari ini?", "What does the elephant carry on its back? What do we see the elephant doing today?")
            ),
            imageResId = R.drawable.gajah
        )
    )
}
