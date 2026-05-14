# EnjoyTrip Design Guide

## Product Direction

EnjoyTrip is a travel platform where users discover tourist attractions, create personal travel plans, record visited places on a map, and share travel stories with others.

The website should begin with an emotional landing page and then guide users into practical map-based discovery, planning, recording, and community features.

For page-level behavior, data contracts, and route-specific requirements, see [FRONTEND_SPEC.md](./FRONTEND_SPEC.md).

## Design Reference

EnjoyTrip should reference Airbnb's consumer travel experience in spirit, not copy it directly.

Adopt these patterns:

- Photography-led first impression
- Clean white canvas with restrained borders
- One memorable primary accent color
- Soft rounded search and card components
- Dense but readable place cards after the first viewport
- Navigation that feels simple even when the service has multiple features

Avoid Airbnb-specific booking patterns such as reservation rails, nightly price emphasis, guest count controls, or accommodation-first language.

## Brand Tone

EnjoyTrip should feel warm, personal, and trustworthy. The tone is more emotional than a map utility, but more practical than a travel diary.

Use this product feeling:

> 따뜻한 여행 감성 + 정돈된 지도/계획 UI

The interface should make users feel that travel can begin as a search, become a plan, and remain as a memory.

## Landing Message

### Main Copy

여행의 시작부터 추억이 되는 순간까지, EnjoyTrip과 함께하세요.

### Supporting Copy

가고 싶은 여행지를 찾고, 나만의 일정을 만들고, 다녀온 장소와 사진을 지도 위에 남겨보세요.

## Recommended Frontend Stack

Use a separate SPA frontend that consumes the existing Spring Boot REST API.

- Framework: React
- Build tool: Vite
- Language: TypeScript
- Styling: Tailwind CSS
- UI foundation: shadcn/ui components customized to EnjoyTrip tokens
- Icons: Lucide React
- Map: Kakao Maps JavaScript API
- API client: Fetch API or Axios with a small shared API module
- Routing: React Router

Backend remains Spring Boot with MySQL/MyBatis. The frontend should not be coupled to JSP page rendering.

## Design Tokens

### Color

Use one primary accent color consistently for major calls to action, active navigation states, selected map markers, and important save/register actions.

- Primary: Coral Rose `#FF5A66`
- Primary Active: Deep Coral `#E94350`
- Primary Soft: Pale Coral `#FFE3E6`
- Secondary Accent: Fresh Green `#2FBF71`
- Informational Accent: Sky Blue `#2F80ED`
- Text: Ink `#222222`
- Text Muted: Soft Ink `#6B7280`
- Canvas: White `#FFFFFF`
- Surface Soft: Warm Gray `#F7F7F7`
- Border: Hairline Gray `#DDDDDD`
- Error: Red `#DC2626`
- Warning: Amber `#F59E0B`
- Success: Green `#16A34A`

Primary color should be used sparingly. Most screens should remain white, image-led, and content-led.

Use Coral Rose for primary CTAs, selected navigation states, the search button, save/register active states, and the most important map marker state. Do not use multiple competing warm accent colors on the same screen.

### Font

Use `Pretendard` as the primary Korean UI font.

```css
font-family: "Pretendard", -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
```

Guidelines:

- Use one font family across the product.
- Avoid decorative fonts, handwritten fonts, and heavy display fonts.
- Use font weight to create hierarchy, not multiple font families.
- Keep body text at `14px` to `16px`.
- Keep metadata at `12px` to `14px`.
- Hero copy can use `40px` to `56px` on desktop depending on image composition.

### Typography

- Landing hero title: large but not poster-like
- Section headings: compact and confident
- Card titles: readable and slightly bold
- Metadata: small, muted, and scannable
- Button text: medium weight

Typography should be modest. Let photos, maps, and place cards carry the emotional weight.

### Shape

Use soft rounded corners throughout:

- Search bars: full pill shape
- Primary buttons: 8px to 12px radius
- Place cards and image masks: 12px to 16px radius
- Icon buttons and map controls: circular

Avoid sharp enterprise-style rectangles.

### Spacing

- Base spacing unit: `4px`
- Compact control gap: `8px`
- Form field gap: `12px` to `16px`
- Card internal padding: `16px` to `24px`
- Page section vertical padding: `64px` desktop
- Main content max width: about `1200px` to `1280px`

Use denser spacing for board/admin tables and more generous spacing for landing and photo-led sections.

### Elevation

Keep elevation restrained.

- Default surfaces: no shadow
- Cards: border or very subtle shadow
- Dropdowns, modals, floating search surfaces: one soft shadow tier
- Avoid stacked shadow systems or heavy floating panels

## Visual Principles

### Canvas

Use a mostly white or very light warm canvas. The page should feel bright, open, and travel-friendly. Avoid heavy dark sections, saturated gradients, and decorative background blobs.

### Photography

Photography is the main emotional asset. Use real travel imagery, destination photos, user memories, or map-connected place thumbnails.

Images should reveal actual places or travel moments. Avoid generic blurred scenery that does not help users understand the service.

### Map UI

Use Kakao Maps JavaScript API for map rendering and interaction.

- Official Tourist Attractions and Hot Places must use distinct marker styles.
- Selected markers should be clearly larger or use Coral Rose.
- Route lines in Travel Plan should use Sky Blue or muted blue.
- Map panels should avoid covering essential map controls.
- Place list selection and map marker selection must stay synchronized.

## UI Components

### Component Foundation

Use shadcn/ui as an editable component foundation, customized to EnjoyTrip tokens.

Recommended base components:

- Button
- Input
- Select
- Dialog
- Dropdown Menu
- Navigation Menu
- Card
- Badge
- Tabs
- Table
- Pagination
- Textarea
- Calendar or Date Picker
- Tooltip
- Alert Dialog

### Component Rules

- Primary actions use Coral Rose.
- Destructive actions use a clear danger treatment and confirmation dialog.
- Icon-only actions must have tooltips or accessible labels.
- Cards should be photo-first for tourist attractions and Hot Places.
- Admin and board screens can use denser tables/lists.
- Forms should use clear labels, helper/error text, and consistent spacing.

### Global Navigation

Use a clean top navigation over a white surface.

- Left: EnjoyTrip logo
- Center: 관광지, 여행 계획, 핫플레이스, 게시판
- Right guest state: 로그인
- Right member state: 마이페이지, 로그아웃
- Right administrator state: 관리자, 마이페이지, 로그아웃

Header navigation should move directly to full feature pages. The logo returns to the landing page.

### Search Bar

Use a large pill-shaped search bar on the landing page and a more compact version on listing pages.

Suggested fields:

- 시/도
- 군/구
- 장소 유형

The search button should be circular, icon-led, and use Coral Rose.

### Cards

Place cards should be photo-first.

Tourist Attraction cards:

1. Image
2. Place name
3. Region/address
4. Place type
5. Optional stats or short description

Hot Place cards:

1. User photo or uploaded travel photo
2. Place name
3. Travel period
4. Place type
5. Short description
6. Map/location cue

Use save/register/edit/delete as icon buttons where the meaning is clear.

### Map Markers

- Official Tourist Attraction: primary or blue location marker
- Hot Place: coral or photo-pin marker
- Selected place: larger marker with clear active state
- Travel Plan route point: numbered marker

## Icon System

Use Lucide icons with a consistent outline style.

Defaults:

- Normal UI icons: `20px`
- Dense metadata/table actions: `16px`
- Feature cards/empty states: `24px`
- Stroke width: `2`
- Style: outline, not filled

Suggested mapping:

- Search: `Search`
- Map/location: `Map`, `MapPin`
- Travel plan: `Route`
- Hot Place/photo: `Camera`, `Image`, `MapPinned`
- Board/community: `MessagesSquare`, `ClipboardList`
- Login/member: `User`, `LogIn`, `LogOut`
- My Page: `CircleUserRound`
- Admin: `ShieldCheck`
- Add: `Plus`
- Edit: `Pencil`
- Delete: `Trash2`
- Save: `Save`
- Calendar/travel period: `CalendarDays`
- Budget: `Wallet`
- Memo: `NotebookPen`
- More menu: `MoreHorizontal`

Avoid mixing icon libraries.

## Interaction States

Every interactive surface should define the following states where relevant.

### Loading

- Use skeletons for cards, lists, and tables.
- Use a small spinner only for local actions such as save, upload, or delete.
- Map loading should show a neutral map placeholder and short status text.

### Empty

- Empty states should explain what is missing and provide the next action.
- 관광지 검색 결과 없음: suggest changing region or place type.
- 여행 계획 없음: show a clear "새 여행 계획 만들기" action.
- 핫플레이스 없음: show a clear "핫플레이스 등록하기" action.
- 게시글 없음: keep the board structure visible and show a write action if signed in.

### Error

- API failure should show a short, human-readable message and retry action.
- Kakao Map load failure should keep the page usable with the list panel if data exists.
- Photo upload failure should show the failed file and allow retry/removal.
- Delete failure should close no data locally until the server confirms success.

### Unauthorized

- When a guest attempts a protected action, open the login modal in-place.
- After successful login, return the user to the attempted action when feasible.
- If a user lacks permission, show a clear permission message and do not show owner/admin actions.

### Saving/Saved

- Save buttons should show an in-progress state while submitting.
- Saved state should be confirmed with toast or inline status.
- Prevent duplicate submissions during saving.

### Delete Confirmation

- Delete actions require confirmation.
- Confirmation copy should name the content type: travel plan, Hot Place, board post, or notice.
- Destructive buttons should use the danger token, not Coral Rose.

## Responsive Rules

The current design target is desktop web, but layouts must keep a minimum responsive fallback so future mobile work does not require a full rewrite.

- Desktop `>= 1024px`: map + side panel, multi-column card grids, full header navigation.
- Tablet `768px - 1023px`: map above or beside a narrower list, reduced card columns, compact header.
- Mobile fallback `< 768px`: single-column layout, filter drawer or bottom sheet for map/list pages, stacked cards.

Desktop remains the primary quality bar. Mobile fallback should be usable, not fully optimized.

## Accessibility

- Modals must trap focus and return focus to the triggering control when closed.
- Login and sign-up modals must close with `Esc` and visible close controls.
- All icon buttons need accessible labels.
- Interactive map markers need keyboard-accessible equivalent list items.
- Form inputs need visible labels and error messages.
- Color cannot be the only way to distinguish state.
- Text and primary controls must meet reasonable contrast against their backgrounds.
- Pagination, tabs, dropdowns, and dialogs should use accessible shadcn/Radix primitives where possible.

## Design Decisions

- The landing page leads with emotion, not raw API functionality.
- Airbnb is a reference for travel-marketplace clarity, photography, whitespace, and search patterns.
- EnjoyTrip differentiates itself through planning, user-recorded Hot Places, and map-based travel memories.
- The UI should not look like an accommodation booking service.
- Desktop web is the primary design target with minimum responsive fallbacks.
- Login uses a popup/modal.
- Hot Place registration/editing uses dedicated pages.
- Header navigation moves directly to full feature pages.
- Administrator scope is limited to notice management for now; broader administration is deferred.
