document.addEventListener('DOMContentLoaded', function() {
  // Find the GitHub repository link in the header
  // Try different selectors for the repository link
  let repoLink = document.querySelector('.md-header__source');

  // If the first selector doesn't work, try an alternative
  if (!repoLink) {
    repoLink = document.querySelector('.md-header-nav__source');
  }

  // If still not found, try to find the repository button
  if (!repoLink) {
    repoLink = document.querySelector('.md-header__button[title*="repository"], .md-header__button[aria-label*="repository"], .md-header__button[href*="github"]');
  }

  if (repoLink) {
    // Create a new link element for Slack in the header
    const slackLink = document.createElement('a');
    slackLink.href = 'https://kotlinlang.slack.com/messages/koog-agentic-framework/';
    slackLink.className = 'md-header__slack-link';
    slackLink.setAttribute('title', 'Koog on Slack');
    slackLink.setAttribute('aria-label', 'Koog on Slack');
    slackLink.setAttribute('target', '_blank');
    slackLink.setAttribute('rel', 'noopener');

    // Create a span to hold the icon
    const iconSpan = document.createElement('span');
    iconSpan.className = 'md-header__slack-icon';

    // Add the Slack icon SVG
    iconSpan.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512"><!--! Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. --><path d="M94.12 315.1c0 25.9-21.16 47.06-47.06 47.06S0 341 0 315.1c0-25.9 21.16-47.06 47.06-47.06h47.06v47.06zm23.72 0c0-25.9 21.16-47.06 47.06-47.06s47.06 21.16 47.06 47.06v117.84c0 25.9-21.16 47.06-47.06 47.06s-47.06-21.16-47.06-47.06V315.1zm47.06-188.98c-25.9 0-47.06-21.16-47.06-47.06S139 32 164.9 32s47.06 21.16 47.06 47.06v47.06H164.9zm0 23.72c25.9 0 47.06 21.16 47.06 47.06s-21.16 47.06-47.06 47.06H47.06C21.16 243.96 0 222.8 0 196.9s21.16-47.06 47.06-47.06H164.9zm188.98 47.06c0-25.9 21.16-47.06 47.06-47.06 25.9 0 47.06 21.16 47.06 47.06s-21.16 47.06-47.06 47.06h-47.06V196.9zm-23.72 0c0 25.9-21.16 47.06-47.06 47.06-25.9 0-47.06-21.16-47.06-47.06V79.06c0-25.9 21.16-47.06 47.06-47.06 25.9 0 47.06 21.16 47.06 47.06V196.9zM283.1 385.88c25.9 0 47.06 21.16 47.06 47.06 0 25.9-21.16 47.06-47.06 47.06-25.9 0-47.06-21.16-47.06-47.06v-47.06h47.06zm0-23.72c-25.9 0-47.06-21.16-47.06-47.06 0-25.9 21.16-47.06 47.06-47.06h117.84c25.9 0 47.06 21.16 47.06 47.06 0 25.9-21.16 47.06-47.06 47.06H283.1z"/></svg>';

    // Add the icon to the link
    slackLink.appendChild(iconSpan);

    // Insert the Slack link after the GitHub link
    // Use the repoLink we've already found to get the header actions container
    const headerActions = repoLink.parentNode;

    if (headerActions) {
      // If we found the header actions container, append the Slack link to it
      headerActions.appendChild(slackLink);
    } else {
      // Fallback: insert after the GitHub link
      repoLink.parentNode.insertBefore(slackLink, repoLink.nextSibling);
    }
  }
});
