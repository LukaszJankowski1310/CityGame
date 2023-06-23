package com.example.citygame.profile

class UserTitle {


    companion object {
        val titles = listOf<String>(
            "Ordinary Novice", "Beginner Seeker", "Rookie", "City Explorer", "Determined Collector",
            "Seasoned Collector", "Master of Alleys", "Discoverer of the Century", "Poznan Legend", "Golden Pyra"
        )

          fun getTitle(num :Int) : String  {
            val title =  when  {
                (num < titles.size) -> titles[num]
                else -> "Golden Pyra"
            }
            return title
        }
    }

}

