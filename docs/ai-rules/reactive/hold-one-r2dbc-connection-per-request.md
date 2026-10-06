# Hold one R2DBC connection per request

Inside one HTTP request, run repository queries one after another so the request holds a single pooled connection. Give a section its own endpoint only when that section must render without waiting for the rest of the screen.

## Good

```kotlin
homeRepository.loadOverviews(userId, language, window)
    .flatMap { snapshot ->
        homeRepository.loadRecentWords(userId, language, limit).collectList()
            .flatMap { words ->
                homeRepository.loadRecentConversations(userId, language, limit).collectList()
                    .map { conversations ->
                        snapshot.toHomeResponse(window).copy(
                            recentContent = HomeRecentContent(words, conversations),
                        )
                    }
            }
    }
```

`GET /api/v1/home/activity` is a second request with its own single query.

## Bad

```kotlin
Mono.zip(
    homeRepository.loadOverviews(...),
    homeRepository.loadRecentWords(...).collectList(),
    homeRepository.loadRecentConversations(...).collectList(),
    homeRepository.loadActivityDays(...).collectList(),
)
```

One landing request checks out four connections. The default pool has ten.
