# Architecture of data sources

The problem is that there is many different data sources, which uses different
methods for getting data, some uses a simple HTML page or a JSON Api which
should be easy to scrape in a general fashion.
Examples of such sources is RoyalRoad.com or reaperscans.com.

But other sites uses much more complicated methods of getting data, such as
gRPC-Web which is hard to generalize across different sources. Not to mention
just hard to decipher as the .proto file might be needed. But a full web client
such as playwright/selenium might be able to just look at the rendered html
instead.

The decision architecture comes down to 3 main possible choices as I can see it

## Monolith App

Have everything in a single app which should make it easier to add new kinds of
sources, as they all exist in the same app.
But it would make it much harder to test the scraper compared to now, as it
would become a part of the android application.
Updating a source would also require updating the entire app, which is slow.

## Plugins

Have each source is implemented in a plugin which uses functions provided by the
main app. This would make it easier to update each source individually and
customize them as required
