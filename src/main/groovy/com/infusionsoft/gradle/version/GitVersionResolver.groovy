package com.infusionsoft.gradle.version

import com.google.common.base.Joiner
import com.google.common.base.Preconditions
import com.google.common.base.Splitter
import com.google.common.base.Supplier
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.errors.IncorrectObjectTypeException
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTag
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.GradleScriptException

import java.util.regex.Matcher
import java.util.regex.Pattern

public class GitVersionResolver {

    private final String versionSplitter
    private final String releaseTagPattern
    private final String matchGroup
    private final String releaseSuffix
    private final String snapshotSuffix
    private final String releaseTagIfNone
    private final Pattern compiledPattern

    final Multimap<ObjectId, String> namedCommits
    final Repository repo;


    public GitVersionResolver(String projectPath, TagOptions tagSomething) throws IOException, GitAPIException {
        Preconditions.checkNotNull(projectPath)
        Preconditions.checkNotNull(tagSomething)
        versionSplitter = tagSomething.getVersionSplitter()
        releaseTagPattern = tagSomething.getReleaseTagPattern()
        matchGroup = tagSomething.getGroupMatcher()
        releaseSuffix = tagSomething.getReleaseSuffix()
        snapshotSuffix = tagSomething.getSnapshotSuffix()
        releaseTagIfNone = tagSomething.getReleaseTagIfNone()
        compiledPattern = Pattern.compile(releaseTagPattern)

        repo = new FileRepositoryBuilder()
                .setWorkTree(new File(projectPath))
                .findGitDir()
                .build()
        namedCommits = mapOfCommits()
    }

    public String getVersion(boolean isRelease) throws IOException, GitAPIException {
        final String version
        if (isRelease) {
            version = newReleaseUseCase()
        } else if (isHeadTaggedAsRelease()) {
            version = existingReleaseUseCase()
        } else {
            version = developerUseCase()
        }
        return version
    }

    private class VersionDescription {
        private String version
        private String tagName

        VersionDescription(String version, String tagName) {
            this.version = version
            this.tagName = tagName
        }
    }

    private VersionDescription plusOneUseCase() throws IOException, GitAPIException {
        String releaseTag = findMostRecentTagMatch()

        String version = releaseTag.replaceAll(releaseTagPattern, matchGroup)


        ArrayList<String> versionElement = Lists.newArrayList(Splitter.on(versionSplitter).split(version))

        Integer lastElement = Integer.parseInt(versionElement.remove(versionElement.size() - 1))
        versionElement.add(String.valueOf(lastElement + 1))
        String incrementedVersion = Joiner.on(versionSplitter).join(versionElement)

        String incrementedReleaseTag = releaseTag.replaceAll(releaseTagPattern) { fullTag, versionGroup ->
            return fullTag.replaceAll(versionGroup, incrementedVersion)
        }

        return new VersionDescription(incrementedVersion, incrementedReleaseTag)
    }

    private String developerUseCase() throws IOException, GitAPIException {
        return plusOneUseCase().version + snapshotSuffix
    }

    private String newReleaseUseCase() throws IOException, GitAPIException {
        VersionDescription versionDescription = plusOneUseCase()
        tagRepo(versionDescription.tagName)
        return versionDescription.version + releaseSuffix
    }

    private String existingReleaseUseCase() throws IOException, GitAPIException {
        String releaseTag = findMostRecentTagMatch()
        return releaseTag.replaceAll(releaseTagPattern, matchGroup) + releaseSuffix
    }

    private boolean isReleaseTag(String tagCandidate) {
        final Matcher releaseTagMatcher = compiledPattern.matcher(tagCandidate)
        return releaseTagMatcher.matches()
    }

    private void tagRepo(String tagName) throws GitAPIException {
        if (hasReleaseTagOnHead()) {
            throw new GradleScriptException("A commit should have at most a single release version", new IllegalStateException())
        } else {
            final Git git = new Git(repo)
            git.tag().setName(tagName).call()
        }
    }

    private boolean hasReleaseTagOnHead() {
        boolean isRelease = false
        ObjectId head = repo.resolve("HEAD")
        RevWalk walk = new RevWalk(repo)
        RevCommit headCommit = walk.parseCommit(head)
        final Collection<String> objectTags = namedCommits.get(headCommit.getId())

        if (objectTags != null && objectTags.size() > 0) {
            for (String tagName : objectTags) {
                final Matcher releaseTagMatcher = compiledPattern.matcher(tagName)
                if (releaseTagMatcher.matches()) {
                    isRelease = true
                    break
                }
            }
        }

        return isRelease
    }

    private boolean isHeadTaggedAsRelease() throws IOException {
        ObjectId head = repo.resolve("HEAD")
        final Collection<String> tags = namedCommits.get(head)
        if (tags != null && tags.size() != 0) {
            for (String tag : tags) {
                if (isReleaseTag(tag)) {
                    return true
                }
            }
        }
        return false
    }

    private String findMostRecentTagMatch() throws IOException, GitAPIException {
        Pattern compiledPattern = Pattern.compile(releaseTagPattern)

        ObjectId head = repo.resolve("HEAD")
        if (head == null) {
            if (isReleaseTag(releaseTagIfNone)) {
                return releaseTagIfNone
            } else {
                throw new GradleScriptException("Don't do it bro, not valid default tag", new IllegalArgumentException())
            }
        }
        RevWalk walk = new RevWalk(repo)
        RevCommit headCommit = walk.parseCommit(head)

        walk.markStart(headCommit)
        try {
            for (RevCommit commit : walk) {
                final Set<String> matchingTags = new HashSet<String>()
                final Collection<String> objectTags = namedCommits.get(commit.getId())

                if (objectTags != null && objectTags.size() > 0) {
                    for (String tagName : objectTags) {
                        final Matcher releaseTagMatcher = compiledPattern.matcher(tagName)
                        if (releaseTagMatcher.matches()) {
                            matchingTags.add(tagName)
                        }
                    }
                }

                if (matchingTags.size() > 0) {
                    if (matchingTags.size() > 1) {
                        throw new GradleScriptException("A commit should have at most a single release version", new IllegalStateException())
                    }
                    return matchingTags.iterator().next()
                }

            }
        } finally {
            walk.release()
        }
        if (isReleaseTag(releaseTagIfNone)) {
            return releaseTagIfNone
        } else {
            throw new GradleScriptException("Don't do it bro, not valid default tag", new IllegalArgumentException())
        }

    }

    // map of target objects and the tags they have.
    private Multimap<ObjectId, String> mapOfCommits() throws IOException, GitAPIException {
        final Map<ObjectId, Collection<String>> namedCommits = Maps.newHashMap()
        final Multimap<ObjectId, String> commits = Multimaps.newListMultimap(namedCommits, new Supplier<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList()
            }
        })

        final ObjectId head = repo.resolve("HEAD")
        if (head == null) {
            return commits
        }
        final RevWalk walk = new RevWalk(repo)
        final RevCommit headCommit = walk.parseCommit(head)

        walk.markStart(headCommit)

        for (Ref tagRef : new Git(repo).tagList().call()) {
            walk.reset()

            String revTagName = null
            ObjectId targetObjectId = null
            ObjectId objectId = repo.resolve(tagRef.getName())

            try {
                RevTag revTag = walk.parseTag(objectId)
                if (revTag != null) {
                    targetObjectId = revTag.getObject().getId()
                    revTagName = revTag.getTagName()
                }
            } catch (IncorrectObjectTypeException ignored) {
            }

            if (targetObjectId != null) {
                commits.put(targetObjectId, revTagName)
            }
        }

        walk.release()

        return commits
    }
}