# EnjoyTrip Frontend Spec

This document captures page-level behavior, data requirements, routing expectations, and user states for the EnjoyTrip frontend.

For visual language, component styling, tokens, icons, and accessibility principles, see [DESIGN.md](./DESIGN.md).

## Product Scope

EnjoyTrip supports:

1. Region-based tourist attraction discovery with Kakao Map
2. Personal travel plan creation and editing
3. Hot Place registration with photos, travel period, place type, location, and description
4. Community board posts with administrator notices pinned at the top
5. Login, sign-up, My Page, and administrator notice management

## Screen Inventory

1. Landing page
2. Attraction discovery page
3. Travel plan list page
4. Travel plan detail/edit page
5. Hot Place list page
6. Hot Place detail page
7. Hot Place registration/edit page
8. Board list page
9. Board post detail page
10. Board post write/edit page
11. My Page
12. Administrator page
13. Login popup
14. Sign-up popup

## Route Suggestions

- `/`: Landing page
- `/attractions`: Attraction discovery page
- `/plans`: Travel plan list page
- `/plans/new`: New travel plan page
- `/plans/:planId`: Travel plan detail/edit page
- `/hotplaces`: Hot Place list page
- `/hotplaces/new`: Hot Place registration page
- `/hotplaces/:hotPlaceId`: Hot Place detail page
- `/hotplaces/:hotPlaceId/edit`: Hot Place edit page
- `/board`: Board list page
- `/board/new`: Board post write page
- `/board/:postId`: Board post detail page
- `/board/:postId/edit`: Board post edit page
- `/mypage`: My Page
- `/admin`: Administrator page

Login and sign-up are modal routes or UI state overlays, not dedicated pages.

## Landing Page

### Purpose

Introduce EnjoyTrip emotionally, then provide direct entry into the main features.

### Required Sections

1. Emotional Hero
2. Attraction search entry
3. Service flow: find, plan, record, share
4. Map-based attraction discovery preview
5. Travel plan preview
6. Hot Place preview
7. Board/community preview
8. Final CTA

### Primary Copy

Main copy:

> 여행의 시작부터 추억이 되는 순간까지, EnjoyTrip과 함께하세요.

Supporting copy:

> 가고 싶은 여행지를 찾고, 나만의 일정을 만들고, 다녀온 장소와 사진을 지도 위에 남겨보세요.

### Navigation Behavior

Header links move directly to full feature pages, even from the landing page.

## Attraction Discovery Page

### Purpose

Let users select a province/city, district, and place type, then view matching places on Kakao Map and in a result list.

### Primary Controls

1. Province/city selector
2. District selector
3. Place type selector
4. Search action

Supported place types:

- 관광지
- 문화시설
- 축제/공연/행사
- 여행코스
- 레포츠
- 숙박
- 쇼핑
- 음식점

### Layout

- Desktop: filter/search panel and result list on the left, Kakao Map on the right.
- Tablet: map top with result list below or a narrower side panel.
- Mobile fallback: filter drawer or bottom sheet, single-column results.

### Behavior

- Selecting a province/city loads district options.
- Selecting a district and place type updates the map focus and result markers.
- Selecting a marker highlights the matching place card.
- Selecting a place card focuses the corresponding map marker.
- Search is primarily structured selection. Keyword search can be added as an optional advanced field.

### States

- Loading: skeleton result list and map placeholder.
- Empty: show "조건에 맞는 관광지가 없습니다" and suggest changing region or place type.
- Error: show retry action for API failures.
- Map error: show list results if available and explain that the map could not load.

## Travel Plan List Page

### Purpose

Show the user's existing travel plans before entering the map-based editor.

### Core Actions

- View an existing travel plan
- Create a new travel plan
- Edit a travel plan
- Delete a travel plan

### Plan Card Fields

- Plan title
- Representative region or route summary
- Number of places
- Estimated budget if available
- Last updated date or created date
- Small map or route thumbnail if feasible

### Behavior

- Clicking the main body of a plan card opens the detail/edit page.
- Edit and delete actions are visible but secondary.
- Delete requires confirmation.
- Guests attempting to create a plan should see the login modal.

## Travel Plan Detail/Edit Page

### Purpose

Provide the main map-based route planning workspace.

### Core Actions

- Add places through search
- Reorder selected places
- View route lines on the map
- Edit budget
- Edit memo
- Save changes
- Delete the plan

### Layout

- Desktop: large map area with a side route editor panel.
- Tablet: map top, route editor below or collapsible.
- Mobile fallback: map with route list in a bottom sheet.

### Route Editor Panel

- Ordered place list with visible sequence numbers
- Drag handle or move controls for changing order
- Place name, short address, and type
- Remove action as an icon button
- Budget input area
- Memo area

### Map Behavior

- Each selected place has a numbered marker.
- Straight-line route segments connect markers in the current order.
- Reordering the list updates marker numbers and route lines.
- Selecting a place in the side panel highlights its marker.
- Selecting a marker highlights its place in the side panel.

### States

- Saving: disable duplicate saves and show progress.
- Saved: show toast or inline confirmation.
- Delete: require confirmation.
- Unauthorized: open login modal for protected actions.

## Hot Place List Page

### Purpose

Show shared visited-place records before users enter detail or registration flows.

### Core Actions

- View shared Hot Places
- Add a new Hot Place
- Delete own Hot Places
- Open Hot Place detail

### Card Fields

- Main photo thumbnail
- Place name
- Travel period
- Place type
- Short description preview
- Author if community visibility is needed
- Location cue or small map hint

### Behavior

- Clicking a card opens the Hot Place detail page.
- Users can delete only their own Hot Places.
- Delete requires confirmation.
- If map filtering is present, selecting a location or marker filters or highlights related cards.

### States

- Empty: show "등록된 핫플레이스가 없습니다" and a registration CTA.
- Upload-related errors appear on registration/edit pages, not list cards.
- Permission denied: hide edit/delete for non-owners.

## Hot Place Detail Page

### Purpose

Present one shared travel record with photos and map context.

### Content

- Photo gallery
- Place name
- Map location
- Travel period
- Place type
- Description
- Author
- Edit/delete actions for the owner

### Behavior

- Owner can edit or delete.
- Non-owner can view only.
- Map marker should identify the recorded location.

## Hot Place Registration/Edit Page

### Purpose

Allow users to create or update a shared visited-place record.

Registration and editing use dedicated pages, not popups.

### Form Order

1. Search/select visited place
2. Upload photos
3. Enter travel period
4. Select place type
5. Write short description
6. Save

### States

- Uploading: show file-level progress or pending state.
- Upload failure: show failed file, retry, and remove actions.
- Save failure: preserve form data and show retry.
- Unauthorized: open login modal or redirect back after login depending on routing implementation.

## Board List Page

### Purpose

Provide the main community entry point with administrator notices pinned at the top.

### Structure

- Pinned administrator notices at the top
- Regular community posts below
- Pagination at the bottom
- Search and optional category/filter controls
- Write button for signed-in users

### Row Fields

- Notice badge or category
- Title
- Author
- Created date
- View count or comment count if supported

### Behavior

- Clicking a row opens the post detail page.
- Administrator notices remain compact and visually distinct.
- Regular posts use consistent row density for quick scanning.

## Board Post Detail Page

### Purpose

Show the full content of one notice or community post.

### Content

- Title
- Author
- Created date
- Body content
- Attachments or images if supported later
- Back to list action
- Edit/delete actions for the owner or administrator

For administrator notices, keep the "공지" label visible near the title.

## Board Post Write/Edit Page

### Purpose

Provide a focused writing surface.

### Fields

- Title
- Body
- Optional category if supported
- Optional notice mode for administrators only

### Actions

- Save/register
- Cancel
- Delete on edit, if allowed

### Permissions

- Guests attempting to write should see the login modal.
- Only administrators can create notices.
- Owners and administrators can edit/delete according to backend rules.

## Authentication

### Login Popup

Login must use a popup/modal, not a dedicated login page.

Fields:

- User ID
- Password
- Primary login action
- Link or secondary action for sign-up
- Clear error message area

Behavior:

- Protected actions open the login modal in-place.
- After successful login, continue the attempted action when feasible.
- Login errors stay inside the modal.

### Sign-Up Popup

Sign-up should open as a related modal flow.

After successful sign-up, guide the user toward login or My Page depending on session behavior.

### Header States

- Guest: 로그인
- Signed-in member: 마이페이지, 로그아웃
- Administrator: 관리자, 마이페이지, 로그아웃

Logout returns the header to guest state.

## My Page

### Purpose

Provide a personal activity hub for signed-in members.

### Suggested Content

- My travel plans
- My Hot Places
- My board posts
- Recent activity summary
- Member information area

### Behavior

- Users can quickly view, edit, or continue their own content.
- My Page is separate from administrator pages.
- It should feel like a compact personal dashboard, not a public profile.

## Administrator Page

### Purpose

Provide administrator-only service operation screens.

Current scope:

- Notice creation and management

Deferred:

- Board post moderation
- Member management
- Content management
- Service update management

### Navigation

- Administrators can still access My Page for personal member activity.
- Administrator-only functions live under a separate admin entry such as "관리자" or "관리".
- Regular members must not see administrator navigation.

## Data Contracts

These fields define the minimum frontend display contract. Backend DTO names may differ.

### Attraction Card

Required:

- `contentId`
- `title`
- `contentTypeId`
- `addr1`
- `mapx`
- `mapy`

Optional:

- `addr2`
- `firstImage`
- `firstImage2`
- `tel`
- `overview`
- `readcount`
- `sidoCode`
- `gugunCode`

### Hot Place

Required:

- `hotPlaceId`
- `placeName`
- `mapx`
- `mapy`
- `travelStartDate`
- `travelEndDate`
- `placeType`
- `description`
- `authorId`

Optional:

- `photos`
- `address`
- `createdAt`
- `updatedAt`

### Travel Plan

Required:

- `planId`
- `title`
- `ownerId`
- `places`

Each place requires:

- `placeId`
- `name`
- `mapx`
- `mapy`
- `order`

Optional:

- `budget`
- `memo`
- `regionSummary`
- `createdAt`
- `updatedAt`

### Board Post

Required:

- `postId`
- `title`
- `body`
- `authorId`
- `authorName`
- `createdAt`
- `isNotice`

Optional:

- `category`
- `viewCount`
- `commentCount`
- `attachments`
- `updatedAt`

### Auth User

Required:

- `userId`
- `userName`
- `role`

Allowed roles:

- `GUEST`
- `MEMBER`
- `ADMIN`

Optional:

- `email`
- `profileImage`

## Global Interaction States

### Loading

- Use page-level skeletons for lists and cards.
- Use local button loading state for save/delete/upload.
- Map loading should not block non-map content if data is already available.

### Empty

- Empty state must include the next useful action.
- Keep surrounding page structure visible.

### Error

- API errors need retry.
- Form errors need field-level feedback where possible.
- Map errors should not erase list data.

### Unauthorized

- Protected create/edit/delete actions open login modal for guests.
- Permission denied hides unavailable actions and shows a clear message if directly accessed.

### Delete

- Delete requires confirmation.
- Do not optimistically remove content unless rollback is implemented.

## Responsive Rules

Desktop is the primary target, but minimum fallback behavior is required.

- Desktop `>= 1024px`: map + side panel, multi-column card grids, full header navigation.
- Tablet `768px - 1023px`: map top or narrower side panel, reduced card columns.
- Mobile fallback `< 768px`: single-column layout, filter drawer or bottom sheet for map/list pages.

Mobile fallback should be usable, not fully polished.
